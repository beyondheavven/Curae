package com.vention.curae.payment.exception;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.vention.curae.wrapper.ErrorCode;
import com.vention.curae.wrapper.ResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(StripeException.class)
	public ResponseWrapper<Object> handleStripeException(StripeException ex) {
		return ResponseWrapper.withError(HttpStatus.BAD_REQUEST, ex.getMessage(), ErrorCode.BAD_REQUEST);
	}

	@ExceptionHandler(SignatureVerificationException.class)
	public ResponseWrapper<Object> handleSignatureVerificationException(SignatureVerificationException ex) {
		return ResponseWrapper.withError(HttpStatus.BAD_REQUEST, "Invalid Stripe signature", ErrorCode.BAD_REQUEST);
	}

}
