package com.vention.medfast.webhook.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.vention.medfast.config.StripeProperties;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.webhook.dto.WebhookProcessingDto;
import com.vention.medfast.webhook.service.WebhookService;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequestMapping(ApiPaths.WEBHOOK_ENDPOINT)
@RequiredArgsConstructor
public class WebhookController {

	private final StripeProperties stripeProperties;

	private final WebhookService webhookService;

	@PostMapping()
	@Operation(summary = "Stripe webhook endpoint", description = "Endpoint for Stripe webhook")
	public ResponseWrapper<WebhookProcessingDto> getStripeWebhook(@RequestBody String payload,
			@RequestHeader(value = "Stripe-Signature", required = true) String stripeSignature)
			throws SignatureVerificationException, Exception {

		String endpointSecret = stripeProperties.getWebhookSecret();

		Event event = Webhook.constructEvent(payload, stripeSignature, endpointSecret);

		WebhookProcessingDto result = webhookService.processEvent(event);
		return ResponseWrapper.withStatus(HttpStatus.OK, result);
	}

}
