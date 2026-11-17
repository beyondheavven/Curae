package com.vention.medfast.payment.webhook.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.vention.medfast.payment.config.StripeProperties;
import com.vention.medfast.payment.webhook.dto.WebhookProcessingDto;
import com.vention.medfast.payment.webhook.service.WebhookService;
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
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {

	private final StripeProperties stripeProperties;

	private final WebhookService webhookService;

	@PostMapping
	@Operation(summary = "Stripe webhook endpoint")
	public ResponseWrapper<WebhookProcessingDto> getStripeWebhook(@RequestBody String payload,
			@RequestHeader("Stripe-Signature") String stripeSignature) throws SignatureVerificationException {
		Event event = Webhook.constructEvent(payload, stripeSignature, stripeProperties.getWebhookSecret());
		return ResponseWrapper.withStatus(HttpStatus.OK, webhookService.processEvent(event));
	}

}
