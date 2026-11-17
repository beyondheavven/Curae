package com.vention.medfast.payment.purchase.service;

import com.vention.medfast.payment.purchase.dto.PurchaseReportDto;
import com.vention.medfast.payment.purchase.dto.PurchaseResponseDto;
import com.vention.medfast.payment.purchase.dto.StatusStatisticDto;
import com.vention.medfast.payment.purchase.mapper.PurchaseMapper;
import com.vention.medfast.payment.purchase.model.PaymentStatus;
import com.vention.medfast.payment.purchase.model.Purchase;
import com.vention.medfast.payment.purchase.repository.PurchaseRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

	private final PurchaseRepository purchaseRepository;

	private final PurchaseMapper purchaseMapper;

	@Override
	public Purchase save(Purchase purchase) {
		return purchaseRepository.save(purchase);
	}

	@Override
	public Optional<Purchase> findByStripePaymentIntentId(String stripePaymentIntentId) {
		return purchaseRepository.findByStripePaymentIntentId(stripePaymentIntentId);
	}

	@Override
	public PurchaseReportDto getReport(LocalDateTime from, LocalDateTime to, Pageable pageable) {
		List<StatusStatisticDto> statistics = purchaseRepository.getStatisticsByStatus(from, to);

		Long completedCount = statistics.stream()
			.filter(s -> PaymentStatus.SUCCESS.equals(s.status()))
			.map(StatusStatisticDto::count)
			.findFirst()
			.orElse(0L);

		Long canceledCount = statistics.stream()
			.filter(s -> PaymentStatus.FAILED.equals(s.status()))
			.map(StatusStatisticDto::count)
			.findFirst()
			.orElse(0L);

		Long totalAmount = statistics.stream()
			.map(StatusStatisticDto::amount)
			.reduce(BigDecimal.ZERO, BigDecimal::add)
			.longValue();

		Page<PurchaseResponseDto> transactions = purchaseRepository.findAllByCreatedAtBetween(from, to, pageable)
			.map(purchaseMapper::toDto);

		return new PurchaseReportDto(totalAmount, completedCount, canceledCount, transactions);
	}

}
