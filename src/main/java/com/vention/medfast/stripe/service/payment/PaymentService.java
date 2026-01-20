package com.vention.medfast.stripe.service.payment;

import com.stripe.exception.StripeException;
import com.vention.medfast.stripe.dto.PaymentRequestDto;

public interface PaymentService {

	String createCheckoutSession(PaymentRequestDto requestDto) throws StripeException;

}
