package com.vention.curae.payment.payment.service;

import com.stripe.exception.StripeException;
import com.vention.curae.payment.payment.dto.PaymentRequestDto;

public interface PaymentService {

	String createCheckoutSession(PaymentRequestDto requestDto) throws StripeException;

}
