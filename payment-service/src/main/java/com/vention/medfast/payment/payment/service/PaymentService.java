package com.vention.medfast.payment.payment.service;

import com.stripe.exception.StripeException;
import com.vention.medfast.payment.payment.dto.PaymentRequestDto;

public interface PaymentService {

	String createCheckoutSession(PaymentRequestDto requestDto) throws StripeException;

}
