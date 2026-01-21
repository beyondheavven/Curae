package com.vention.medfast.payment.controller;

import com.stripe.exception.StripeException;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.payment.dto.PaymentRequestDto;
import com.vention.medfast.payment.service.PaymentService;
import com.vention.medfast.wrapper.ErrorCode;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

	@Operation(summary = "Create checkout session",
			description = "Creates a Stripe Checkout Session based on the provided service and patient ID. Returns a URL to redirect the user to Stripe's hosted payment page.")
	@PostMapping("/checkout")
	public ResponseWrapper<String> createCheckoutSession(@RequestBody PaymentRequestDto requestDto) {
		try {
			String paymentUrl = paymentService.createCheckoutSession(requestDto);
			return ResponseWrapper.ok(paymentUrl);
		}
		catch (StripeException e) {
			return ResponseWrapper.withError(HttpStatus.BAD_REQUEST, e.getMessage(), ErrorCode.BAD_REQUEST);
		}
	}

}
