package com.vention.curae.payment.payment.controller;

import com.stripe.exception.StripeException;
import com.vention.curae.payment.payment.dto.PaymentRequestDto;
import com.vention.curae.payment.payment.service.PaymentService;
import com.vention.curae.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Endpoints for processing payments via Stripe")
public class PaymentController {

	private final PaymentService paymentService;

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/checkout")
	@Operation(summary = "Create checkout session", description = "Creates a Stripe Checkout Session")
	public ResponseWrapper<String> createCheckoutSession(@Valid @RequestBody PaymentRequestDto requestDto)
			throws StripeException {
		return ResponseWrapper.ok(paymentService.createCheckoutSession(requestDto));
	}

}
