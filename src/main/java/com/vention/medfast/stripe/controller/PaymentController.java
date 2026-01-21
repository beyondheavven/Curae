package com.vention.medfast.stripe.controller;

import com.stripe.exception.StripeException;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.stripe.dto.PaymentRequestDto;
import com.vention.medfast.stripe.service.payment.PaymentService;
import com.vention.medfast.wrapper.ErrorCode;
import com.vention.medfast.wrapper.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.PAYMENT_ENDPOINT)
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

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
