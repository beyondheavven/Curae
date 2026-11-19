package com.vention.curae.payment.purchase.service;

import com.vention.curae.payment.purchase.dto.PurchaseReportDto;
import com.vention.curae.payment.purchase.model.Purchase;
import java.time.LocalDateTime;
import java.util.Optional;

public interface PurchaseService {

	Purchase save(Purchase purchase);

	Optional<Purchase> findByStripePaymentIntentId(String stripePaymentIntentId);

	PurchaseReportDto getReport(LocalDateTime from, LocalDateTime to,
			org.springframework.data.domain.Pageable pageable);

}
