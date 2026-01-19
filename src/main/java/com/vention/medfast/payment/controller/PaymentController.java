package com.vention.medfast.payment.controller;

import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.PAYMENT_ENDPOINT)
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${STRIPE_PUBLIC_KEY}")
    private String stripePublicKey;


    @PostMapping
    public String checkout(){
        return "";
    }


}
