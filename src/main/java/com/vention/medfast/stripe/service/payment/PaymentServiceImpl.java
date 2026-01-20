package com.vention.medfast.stripe.service.payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.service.repository.ProvidedServiceRepository;
import com.vention.medfast.stripe.dto.PaymentRequestDto;
import jakarta.annotation.PostConstruct;
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

    public static final String PAYMENT_SUCCESS_URL = "http://localhost:8080/payment-success.html";

    public static final String PAYMENT_CANCEL_URL = "http://localhost:8080/payment-cancel.html";

	@Transactional(readOnly = true)
	@Override
	public String createCheckoutSession(PaymentRequestDto requestDto) throws StripeException {
		ProvidedService providedService = providedServiceRepository.findById(requestDto.serviceId())
			.orElseThrow(() -> new EntityNotFoundException("Service not found"));

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
			.putMetadata("serviceId", providedService.getId().toString())
			.putMetadata("serviceName", providedService.getName())
			.build();

		Session session = Session.create(params);
		return session.getUrl();
	}

}
