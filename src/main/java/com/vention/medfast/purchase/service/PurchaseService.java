package com.vention.medfast.purchase.service;

import com.vention.medfast.purchase.dto.PurchaseReportDto;
import com.vention.medfast.purchase.model.Purchase;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PurchaseService {

	void save(Purchase purchase);

	Optional<Purchase> findByStripePaymentIntentId(String id);

	PurchaseReportDto getReport(LocalDateTime from, LocalDateTime to, Pageable pageable);

}
