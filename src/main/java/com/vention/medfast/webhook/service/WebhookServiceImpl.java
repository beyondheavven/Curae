package com.vention.medfast.webhook.service;

import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.model.StripeObject;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Charge;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.purchase.model.PaymentStatus;
import com.vention.medfast.purchase.model.Purchase;
import com.vention.medfast.purchase.service.PurchaseService;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.service.service.ProvidedServiceService;
import com.vention.medfast.webhook.dto.WebhookProcessingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {

	private final PurchaseService purchaseService;

	private final PatientService patientService;

	private final ProvidedServiceService providedServiceService;

	@Override
	public WebhookProcessingDto processEvent(Event event) {
		StripeObject stripeObject = deserializeEvent(event);

		if (stripeObject == null) {
			return WebhookProcessingDto.builder().status("ERROR").message("Failed to deserialize").build();
		}

		String description;
		String status;

		switch (event.getType()) {
			case "payment_intent.succeeded":
				description = handlePaymentSucceeded((PaymentIntent) stripeObject);
				status = "SUCCESS";
				break;

			case "payment_intent.payment_failed":
				description = handlePaymentFailed((PaymentIntent) stripeObject);
				status = "FAILED";
				break;

			case "charge.refunded":
				description = handleRefundEvent((Charge) stripeObject);
				status = "REFUNDED";
				break;
			default:
				description = "Event ignored";
				status = "IGNORED";
		}

		return buildResponse(event, status, description);
	}

	private String handlePaymentSucceeded(PaymentIntent intent) {
		saveNewPurchase(intent, PaymentStatus.SUCCESS);
		return String.format("Order paid successfully. Amount: %d %s", intent.getAmount(), intent.getCurrency());
	}

	private String handlePaymentFailed(PaymentIntent intent) {
		saveNewPurchase(intent, PaymentStatus.FAILED);
		String errorMsg = intent.getLastPaymentError() != null ? intent.getLastPaymentError().getMessage()
				: "Unknown error";
		return "Payment failed: " + errorMsg;
	}

	private String handleRefundEvent(Charge charge) {
		updatePurchaseStatusToRefunded(charge);
		return String.format("Payment refunded. Amount: %d %s", charge.getAmountRefunded(), charge.getCurrency());
	}

	private void updatePurchaseStatusToRefunded(Charge charge) {
		String paymentIntent = charge.getPaymentIntent();
		if (paymentIntent == null)
			return;

		Optional<Purchase> purchaseOptional = purchaseService.findByStripePaymentIntentId(charge.getPaymentIntent());
		purchaseOptional.ifPresent(purchase -> {
			purchase.setStatus(PaymentStatus.REFUNDED);
			purchaseService.save(purchase);
		});
	}

	private StripeObject deserializeEvent(Event event) {
		EventDataObjectDeserializer objectDeserializer = event.getDataObjectDeserializer();

		if (objectDeserializer.getObject().isPresent()) {
			return objectDeserializer.getObject().get();
		}
		else {
			try {
				return objectDeserializer.deserializeUnsafe();
			}
			catch (EventDataObjectDeserializationException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private WebhookProcessingDto buildResponse(Event event, String status, String message) {
		return new WebhookProcessingDto(event.getId(), event.getType(), status, message);
	}

	private void saveNewPurchase(PaymentIntent intent, PaymentStatus status) {
		if (purchaseService.findByStripePaymentIntentId(intent.getId()).isPresent()) {
			return;
		}

		Map<String, String> metadata = intent.getMetadata();
		if (metadata == null || !metadata.containsKey("patientId") || !metadata.containsKey("serviceId")) {
			return;
		}

		Long patientId = Long.parseLong(metadata.get("patientId"));
		Long serviceId = Long.parseLong(metadata.get("serviceId"));

		Patient patient = patientService.getById(patientId);

		ProvidedService service = providedServiceService.getProvidedServiceById(serviceId);

		Purchase purchase = new Purchase();
		purchase.setPatient(patient);
		purchase.setProvidedService(service);
		purchase.setStripePaymentIntentId(intent.getId());
		purchase.setAmount(BigDecimal.valueOf(intent.getAmount()).divide(BigDecimal.valueOf(100)));
		purchase.setCurrency(intent.getCurrency());
		purchase.setStatus(status);

		purchaseService.save(purchase);
	}

}
