package com.vention.medfast.payment.webhook.service;

import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.vention.medfast.event.PaymentCompletedEvent;
import com.vention.medfast.event.PaymentFailedEvent;
import com.vention.medfast.payment.event.PaymentEventPublisher;
import com.vention.medfast.payment.purchase.model.PaymentStatus;
import com.vention.medfast.payment.purchase.model.Purchase;
import com.vention.medfast.payment.purchase.service.PurchaseService;
import com.vention.medfast.payment.webhook.dto.WebhookProcessingDto;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {

	private final PurchaseService purchaseService;

	private final PaymentEventPublisher paymentEventPublisher;

	@Override
	public WebhookProcessingDto processEvent(Event event) {
		StripeObject stripeObject = deserializeEvent(event);
		if (stripeObject == null) {
			return new WebhookProcessingDto(event.getId(), event.getType(), "ERROR", "Failed to deserialize");
		}

		String description;
		String status;
		switch (event.getType()) {
			case "payment_intent.succeeded" -> {
				description = handlePaymentSucceeded((PaymentIntent) stripeObject);
				status = "SUCCESS";
			}
			case "payment_intent.payment_failed" -> {
				description = handlePaymentFailed((PaymentIntent) stripeObject);
				status = "FAILED";
			}
			case "charge.refunded" -> {
				description = handleRefundEvent((Charge) stripeObject);
				status = "REFUNDED";
			}
			default -> {
				description = "Event ignored";
				status = "IGNORED";
			}
		}
		return new WebhookProcessingDto(event.getId(), event.getType(), status, description);
	}

	private String handlePaymentSucceeded(PaymentIntent intent) {
		Optional<Purchase> purchase = saveNewPurchase(intent, PaymentStatus.SUCCESS);
		purchase.ifPresent(p -> paymentEventPublisher.paymentCompleted(new PaymentCompletedEvent(p.getId(),
				parseLong(metadata(intent), "appointmentId"), p.getPatientId(), metadata(intent).get("patientEmail"),
				p.getAmount(), p.getCurrency(), p.getStripePaymentIntentId(), Instant.now())));
		return String.format("Order paid successfully. Amount: %d %s", intent.getAmount(), intent.getCurrency());
	}

	private String handlePaymentFailed(PaymentIntent intent) {
		Optional<Purchase> purchase = saveNewPurchase(intent, PaymentStatus.FAILED);
		String errorMsg = intent.getLastPaymentError() != null ? intent.getLastPaymentError().getMessage()
				: "Unknown error";
		purchase.ifPresent(p -> paymentEventPublisher
			.paymentFailed(new PaymentFailedEvent(parseLong(metadata(intent), "appointmentId"), p.getPatientId(),
					metadata(intent).get("patientEmail"), p.getAmount(), errorMsg, Instant.now())));
		return "Payment failed: " + errorMsg;
	}

	private String handleRefundEvent(Charge charge) {
		String paymentIntent = charge.getPaymentIntent();
		if (paymentIntent != null) {
			purchaseService.findByStripePaymentIntentId(paymentIntent).ifPresent(purchase -> {
				purchase.setStatus(PaymentStatus.REFUNDED);
				purchaseService.save(purchase);
			});
		}
		return String.format("Payment refunded. Amount: %d %s", charge.getAmountRefunded(), charge.getCurrency());
	}

	private Optional<Purchase> saveNewPurchase(PaymentIntent intent, PaymentStatus status) {
		if (purchaseService.findByStripePaymentIntentId(intent.getId()).isPresent()) {
			return Optional.empty();
		}

		Map<String, String> metadata = metadata(intent);
		Long patientId = parseLong(metadata, "patientId");
		Long serviceId = parseLong(metadata, "serviceId");
		if (patientId == null || serviceId == null) {
			return Optional.empty();
		}

		Purchase purchase = new Purchase();
		purchase.setPatientId(patientId);
		purchase.setServiceId(serviceId);
		purchase.setStripePaymentIntentId(intent.getId());
		purchase.setAmount(BigDecimal.valueOf(intent.getAmount()).divide(BigDecimal.valueOf(100)));
		purchase.setCurrency(intent.getCurrency());
		purchase.setStatus(status);
		return Optional.of(purchaseService.save(purchase));
	}

	private Map<String, String> metadata(PaymentIntent intent) {
		return intent.getMetadata() == null ? Map.of() : intent.getMetadata();
	}

	private Long parseLong(Map<String, String> metadata, String key) {
		String value = metadata.get(key);
		if (value == null) {
			return null;
		}
		try {
			return Long.parseLong(value);
		}
		catch (NumberFormatException ex) {
			return null;
		}
	}

	private StripeObject deserializeEvent(Event event) {
		EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
		Optional<StripeObject> object = deserializer.getObject();
		if (object.isPresent()) {
			return object.get();
		}
		try {
			return deserializer.deserializeUnsafe();
		}
		catch (EventDataObjectDeserializationException ex) {
			return null;
		}
	}

}
