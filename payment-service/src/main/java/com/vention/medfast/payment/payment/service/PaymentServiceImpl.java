package com.vention.medfast.payment.payment.service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.vention.medfast.payment.catalog.repository.ServiceCatalogRepository;
import com.vention.medfast.payment.config.StripeProperties;
import com.vention.medfast.payment.payment.dto.PaymentRequestDto;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final ServiceCatalogRepository serviceCatalogRepository;

	private final StripeProperties stripeProperties;

	@Override
	public String createCheckoutSession(PaymentRequestDto requestDto) throws StripeException {
		var service = serviceCatalogRepository.findById(requestDto.serviceId())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));

		long amountInCents = service.getPrice().multiply(BigDecimal.valueOf(100)).longValue();

		SessionCreateParams.PaymentIntentData.Builder paymentIntentData = SessionCreateParams.PaymentIntentData
			.builder()
			.putMetadata("patientId", requestDto.patientId().toString())
			.putMetadata("serviceId", service.getId().toString());
		if (requestDto.appointmentId() != null) {
			paymentIntentData.putMetadata("appointmentId", requestDto.appointmentId().toString());
		}
		if (requestDto.patientEmail() != null) {
			paymentIntentData.putMetadata("patientEmail", requestDto.patientEmail());
		}

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
						.setName(service.getName())
						.setDescription(service.getDescription())
						.build())
					.build())
				.build())
			.setPaymentIntentData(paymentIntentData.build())
			.build();

		Session session = Session.create(params);
		return session.getUrl();
	}

}
