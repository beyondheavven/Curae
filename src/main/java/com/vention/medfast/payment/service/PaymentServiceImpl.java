package com.vention.medfast.payment.service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.repository.PatientRepository;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.service.repository.ProvidedServiceRepository;
import com.vention.medfast.payment.dto.PaymentRequestDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final ProvidedServiceRepository providedServiceRepository;

	private final PatientRepository patientRepository;

	@Value("${PAYMENT_SUCCESS_URL}")
	private String PAYMENT_SUCCESS_URL;

	@Value("${PAYMENT_CANCEL_URL}")
	private String PAYMENT_CANCEL_URL;

	@Transactional(readOnly = true)
	@Override
	public String createCheckoutSession(PaymentRequestDto requestDto) throws StripeException {
		ProvidedService providedService = providedServiceRepository.findById(requestDto.serviceId())
			.orElseThrow(() -> new EntityNotFoundException("Service not found"));

		Patient patient = patientRepository.findById(requestDto.patientId())
			.orElseThrow(() -> new EntityNotFoundException("Patient not found"));

		Long amountInCents = providedService.getPrice().multiply(BigDecimal.valueOf(100)).longValue();

		SessionCreateParams params = SessionCreateParams.builder()
			.setMode(SessionCreateParams.Mode.PAYMENT)
			.setSuccessUrl(PAYMENT_SUCCESS_URL)
			.setCancelUrl(PAYMENT_CANCEL_URL)
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
