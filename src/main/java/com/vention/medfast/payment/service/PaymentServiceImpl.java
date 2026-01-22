package com.vention.medfast.payment.service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.vention.medfast.config.StripeProperties;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.payment.dto.PaymentRequestDto;
import com.vention.medfast.service.service.ProvidedServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final ProvidedServiceService providedServiceService;

	private final PatientService patientService;

	private final StripeProperties stripeProperties;

	@Transactional(readOnly = true)
	@Override
	public String createCheckoutSession(PaymentRequestDto requestDto) throws StripeException {
		ProvidedService providedService = providedServiceService.getProvidedServiceById(requestDto.serviceId());

		Patient patient = patientService.getById(requestDto.patientId());

		Long amountInCents = providedService.getPrice().multiply(BigDecimal.valueOf(100)).longValue();

		SessionCreateParams params = SessionCreateParams.builder()
			.setMode(SessionCreateParams.Mode.PAYMENT)
			.setSuccessUrl(stripeProperties.getPaymentSuccessUrl())
			.setCancelUrl(stripeProperties.getPaymentCancelUrl())
			.addLineItem(SessionCreateParams.LineItem.builder()
				.setQuantity(1L)
				.setPriceData(SessionCreateParams.LineItem.PriceData.builder()
					.setCurrency(requestDto.currency())
					.setUnitAmount(amountInCents)
					.setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
						.setName(providedService.getName())
						.setDescription(providedService.getDescription())
						.build())
					.build())
				.build())
			.setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder()
				.putMetadata("patientId", patient.getId().toString())
				.putMetadata("serviceId", providedService.getId().toString())
				.build())
			.build();

		Session session = Session.create(params);
		return session.getUrl();
	}

}
