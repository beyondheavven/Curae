package com.vention.medfast.stripe.service.webhook;

import com.stripe.model.Event;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.repository.PatientRepository;
import com.vention.medfast.purchase.model.PaymentStatus;
import com.vention.medfast.purchase.model.Purchase;
import com.vention.medfast.purchase.repository.PurchaseRepository;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.service.repository.ProvidedServiceRepository;
import com.vention.medfast.stripe.dto.WebhookProcessingDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {

	private final PurchaseRepository purchaseRepository;

	private final PatientRepository patientRepository;

	private final ProvidedServiceRepository providedServiceRepository;

	@Override
	public WebhookProcessingDto processEvent(Event event) {
		Optional<StripeObject> objectOptional = event.getDataObjectDeserializer().getObject();

		if (objectOptional.isEmpty()) {
			log.error("❌ Failed to deserialize Stripe Object for event: {}", event.getId());
			return WebhookProcessingDto.builder()
				.eventId(event.getId())
				.eventType(event.getType())
				.status("ERROR")
				.message("Failed to deserialize Stripe Object")
				.build();
		}

		StripeObject stripeObject = objectOptional.get();
		String description;
		String status = null;

		switch (event.getType()) {
			case "payment_intent.succeeded":
				log.info("💰 Processing payment_intent.succeeded...");
				PaymentIntent successIntent = (PaymentIntent) stripeObject;
				saveNewPurchase(successIntent, PaymentStatus.SUCCESS);
				description = "Order paid successfully. Amount: " + successIntent.getAmount();
				status = "SUCCESS";
				break;

			case "payment_intent.payment_failed":
				log.info("❌ Processing payment_intent.payment_failed...");
				PaymentIntent failedIntent = (PaymentIntent) stripeObject;
				saveNewPurchase(failedIntent, PaymentStatus.FAILED);
				String errorMsg = failedIntent.getLastPaymentError() != null
						? failedIntent.getLastPaymentError().getMessage() : "Unknown error";
				description = "Payment declined: " + errorMsg;
				status = "FAILED";
				break;

			case "charge.refunded":
				log.info("💸 Processing charge.refunded...");
				Charge charge = (Charge) stripeObject;
				handleRefund(charge);
				long amountRefunded = charge.getAmountRefunded();
				description = String.format("Payment refunded. Amount: %d %s", amountRefunded, charge.getCurrency());
				status = "REFUNDED";
				break;
			default:
				log.debug("ℹ️ Ignoring event type: {}", event.getType());
				description = "Event ignored";
				status = "IGNORED";
		}

		return buildResponse(event, status, description);
	}

	private void saveNewPurchase(PaymentIntent intent, PaymentStatus status) {
		if (purchaseRepository.findByStripePaymentIntentId(intent.getId()).isPresent()) {
			log.warn("⚠️ Purchase with Intent ID {} already exists in DB. Skipping duplicate.", intent.getId());
			return;
		}

		Map<String, String> metadata = intent.getMetadata();
		if (metadata == null || !metadata.containsKey("patientId") || !metadata.containsKey("serviceId")) {
			log.warn("⚠️ Metadata is NULL for Intent ID: {}. Cannot link purchase to patient.", intent.getId());
			return;
		}

		Long patientId = Long.parseLong(metadata.get("patientId"));
		Long serviceId = Long.parseLong(metadata.get("serviceId"));

		Patient patient = patientRepository.findById(patientId)
			.orElseThrow(() -> new EntityNotFoundException("Patient not found: " + patientId));

		ProvidedService service = providedServiceRepository.findById(serviceId)
			.orElseThrow(() -> new EntityNotFoundException("Service not found: " + serviceId));

		Purchase purchase = new Purchase();
		purchase.setPatient(patient);
		purchase.setProvidedService(service);
		purchase.setStripePaymentIntentId(intent.getId());
		purchase.setAmount(BigDecimal.valueOf(intent.getAmount()).divide(BigDecimal.valueOf(100)));
		purchase.setCurrency(intent.getCurrency());
		purchase.setStatus(status);

		purchaseRepository.save(purchase);
		log.info("✅ Successfully saved purchase! ID: {}, Patient: {}, Status: {}", intent.getId(), patientId, status);
	}

	private void handleRefund(Charge charge) {
		String paymentIntentId = charge.getPaymentIntent();

		if (paymentIntentId == null) {
			log.warn("⚠️ Refund received but PaymentIntent ID is null. Charge ID: {}", charge.getId());
			return;
		}

		Optional<Purchase> purchaseOpt = purchaseRepository.findByStripePaymentIntentId(paymentIntentId);

		if (purchaseOpt.isPresent()) {
			Purchase purchase = purchaseOpt.get();
			purchase.setStatus(PaymentStatus.REFUNDED);
			purchaseRepository.save(purchase);
			log.info("✅ Updated status to REFUNDED for PaymentIntent: {}", paymentIntentId);
		}
	}

	private WebhookProcessingDto buildResponse(Event event, String status, String message) {
		return WebhookProcessingDto.builder()
			.eventId(event.getId())
			.eventType(event.getType())
			.status(status)
			.message(message)
			.build();
	}

}
