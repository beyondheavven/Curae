package com.vention.medfast.stripe.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.stripe.dto.WebhookProcessingDto;
import com.vention.medfast.stripe.service.webhook.WebhookService;
import com.vention.medfast.wrapper.ErrorCode;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Hidden
@RestController
@RequestMapping(ApiPaths.WEBHOOK_ENDPOINT)
@RequiredArgsConstructor
public class WebhookController {

	@Value("${WEBHOOK_SECRET_KEY}")
	private String endpointSecret;

	private final WebhookService webhookService;

	@PostMapping()
	@Operation(summary = "Stripe webhook endpoint", description = "Endpoint for Stripe webhook")
	public ResponseWrapper<WebhookProcessingDto> getStripeWebhook(@RequestBody String payload,
			@RequestHeader("Stripe-Signature") String stripeSignature) {

		if (endpointSecret == null || endpointSecret.isEmpty()) {
			return ResponseWrapper.withError(HttpStatus.INTERNAL_SERVER_ERROR, "Endpoint secret is not set",
					ErrorCode.SERVER_ERROR);

		}

		if (stripeSignature == null) {
			return ResponseWrapper.withError(HttpStatus.BAD_REQUEST, "Missing Stripe-Signature header",
					ErrorCode.BAD_REQUEST);
		}

		Event event = null;

		try {
			event = Webhook.constructEvent(payload, stripeSignature, endpointSecret);
		}
		catch (SignatureVerificationException e) {
			return ResponseWrapper.withError(HttpStatus.BAD_REQUEST, "Invalid signature", ErrorCode.BAD_REQUEST);
		}
		catch (Exception e) {
			return ResponseWrapper.withError(HttpStatus.BAD_REQUEST, "Invalid Payload", ErrorCode.BAD_REQUEST);
		}

		WebhookProcessingDto result = webhookService.processEvent(event);
		return ResponseWrapper.withStatus(HttpStatus.OK, result);
	}

}
