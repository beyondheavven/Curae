package com.vention.medfast.stripe.service.webhook;

import com.stripe.model.Event;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.vention.medfast.stripe.dto.WebhookProcessingDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WebhookServiceImpl implements WebhookService {

	@Override
	public WebhookProcessingDto processEvent(Event event) {
		Optional<StripeObject> objectOptional = event.getDataObjectDeserializer().getObject();

		if (objectOptional.isEmpty()) {
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
				PaymentIntent successIntent = (PaymentIntent) stripeObject;
				description = "Order paid successfully. Amount: " + successIntent.getAmount();
				status = "SUCCESS";
				break;

			case "payment_intent.payment_failed":
				PaymentIntent failedIntent = (PaymentIntent) stripeObject;
				String errorMsg = failedIntent.getLastPaymentError() != null
						? failedIntent.getLastPaymentError().getMessage() : "Unknown error";
				description = "Payment declined: " + errorMsg;
				status = "FAILED";
				break;

			case "charge.refunded":
				Charge charge = (Charge) stripeObject;
				long amountRefunded = charge.getAmountRefunded();
				description = String.format("Payment refunded. Amount: %d %s", amountRefunded, charge.getCurrency());
				status = "REFUNDED";
				break;
			default:
				description = "Event ignored";
				status = "IGNORED";
		}

		return WebhookProcessingDto.builder()
			.eventId(event.getId())
			.eventType(event.getType())
			.status(status)
			.message(description)
			.build();
	}

}
