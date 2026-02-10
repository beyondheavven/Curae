package com.vention.medfast.payment.controller;

import com.stripe.exception.StripeException;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.payment.dto.PaymentRequestDto;
import com.vention.medfast.payment.service.PaymentService;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.PAYMENT_ENDPOINT)
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Endpoints for processing payments via Stripe")
public class PaymentController {

	private final PaymentService paymentService;

	@Operation(summary = "Create checkout session (admin)",
			description = "Creates a Stripe Checkout Session based on the provided service and patient ID. Returns a URL to redirect the user to Stripe's hosted payment page.")
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/checkout")
	public ResponseWrapper<String> createCheckoutSession(@RequestBody PaymentRequestDto requestDto)
			throws StripeException {
		String paymentUrl = paymentService.createCheckoutSession(requestDto);
		return ResponseWrapper.ok(paymentUrl);
	}

}
