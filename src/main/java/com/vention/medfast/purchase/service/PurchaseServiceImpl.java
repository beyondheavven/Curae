package com.vention.medfast.purchase.service;

import com.vention.medfast.purchase.dto.PurchaseReportDto;
import com.vention.medfast.purchase.dto.PurchaseResponseDto;
import com.vention.medfast.purchase.dto.StatusStatisticDto;
import com.vention.medfast.purchase.mapper.PurchaseMapper;
import com.vention.medfast.purchase.model.Purchase;
import com.vention.medfast.purchase.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

	private final PurchaseRepository purchaseRepository;

	private final PurchaseMapper purchaseMapper;

	@Override
	public void save(Purchase purchase) {
		purchaseRepository.save(purchase);
	}

	@Override
	public Optional<Purchase> findByStripePaymentIntentId(String id) {
		return purchaseRepository.findByStripePaymentIntentId(id);
	}

	@Override
	public PurchaseReportDto getReport(LocalDateTime from, LocalDateTime to, Pageable pageable) {

		List<StatusStatisticDto> statistics = purchaseRepository.getStatisticsByStatus(from, to);

		Long completedCount = statistics.stream()
			.filter(s -> "SUCCESS".equalsIgnoreCase(s.status().toString()))
			.map(StatusStatisticDto::count)
			.findFirst()
			.orElse(0L);

		Long canceledCount = statistics.stream()
			.filter(s -> "FAILED".equalsIgnoreCase(s.status().toString()))
			.map(StatusStatisticDto::count)
			.findFirst()
			.orElse(0L);

		Long totalAmount = statistics.stream()
			.map(StatusStatisticDto::amount)
			.reduce(BigDecimal.ZERO, BigDecimal::add)
			.longValue();

		Page<PurchaseResponseDto> transactionDto = purchaseRepository.findAllByCreatedAtBetween(from, to, pageable)
			.map(purchaseMapper::toDto);

		return new PurchaseReportDto(totalAmount, completedCount, canceledCount, transactionDto);
	}

}
