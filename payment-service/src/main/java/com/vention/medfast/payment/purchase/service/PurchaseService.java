package com.vention.medfast.payment.purchase.service;

import com.vention.medfast.payment.purchase.dto.PurchaseReportDto;
import com.vention.medfast.payment.purchase.model.Purchase;
import java.time.LocalDateTime;
import java.util.Optional;

public interface PurchaseService {

	Purchase save(Purchase purchase);

	Optional<Purchase> findByStripePaymentIntentId(String stripePaymentIntentId);

	PurchaseReportDto getReport(LocalDateTime from, LocalDateTime to,
			org.springframework.data.domain.Pageable pageable);

}
