package com.vention.curae.payment.purchase.mapper;

import com.vention.curae.payment.purchase.dto.PurchaseResponseDto;
import com.vention.curae.payment.purchase.model.Purchase;
import org.springframework.stereotype.Component;

@Component
public class PurchaseMapper {

	public PurchaseResponseDto toDto(Purchase purchase) {
		return new PurchaseResponseDto(purchase.getId(), purchase.getPatientId(), purchase.getServiceId(),
				purchase.getStripePaymentIntentId(), purchase.getAmount(), purchase.getCurrency(),
				purchase.getStatus().name(), purchase.getCreatedAt(), purchase.getUpdatedAt());
	}

}
