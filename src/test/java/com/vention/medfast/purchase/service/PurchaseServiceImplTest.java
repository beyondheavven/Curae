package com.vention.medfast.purchase.service;

import com.vention.medfast.purchase.dto.PurchaseReportDto;
import com.vention.medfast.purchase.dto.PurchaseResponseDto;
import com.vention.medfast.purchase.dto.StatusStatisticDto;
import com.vention.medfast.purchase.mapper.PurchaseMapper;
import com.vention.medfast.purchase.model.PaymentStatus;
import com.vention.medfast.purchase.model.Purchase;
import com.vention.medfast.purchase.repository.PurchaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceImplTest {

	@Mock
	private PurchaseRepository purchaseRepository;

	@Mock
	private PurchaseMapper purchaseMapper;

	@InjectMocks
	private PurchaseServiceImpl purchaseService;

	@Test
	void shouldReturnCorrectReport_whenDataExists() {
		LocalDateTime from = LocalDateTime.of(2026, 2, 1, 0, 0);
		LocalDateTime to = LocalDateTime.of(2026, 2, 28, 23, 59);
		Pageable pageable = PageRequest.of(0, 10);

		StatusStatisticDto statSuccess = new StatusStatisticDto(PaymentStatus.SUCCESS, 2L, BigDecimal.valueOf(300.00));
		StatusStatisticDto statFailed = new StatusStatisticDto(PaymentStatus.FAILED, 1L, BigDecimal.valueOf(50.00));
		when(purchaseRepository.getStatisticsByStatus(from, to)).thenReturn(List.of(statSuccess, statFailed));

		Purchase purchase = new Purchase();
		Page<Purchase> purchasePage = new PageImpl<>(List.of(purchase));
		when(purchaseRepository.findAllByCreatedAtBetween(from, to, pageable)).thenReturn(purchasePage);

		PurchaseResponseDto dto = new PurchaseResponseDto(1L, 1L, 1L, "pi_1", BigDecimal.valueOf(150.0), "USD",
				"SUCCESS", LocalDateTime.now(), LocalDateTime.now());
		when(purchaseMapper.toDto(purchase)).thenReturn(dto);

		PurchaseReportDto report = purchaseService.getReport(from, to, pageable);

		assertThat(report).isNotNull();
		assertThat(report.totalAmount()).isEqualTo(350L);
		assertThat(report.completedPurchasesCount()).isEqualTo(2L);
		assertThat(report.canceledPurchasesCount()).isEqualTo(1L);
		assertThat(report.transactions().getContent()).hasSize(1);
		assertThat(report.transactions().getContent().get(0)).isEqualTo(dto);

		verify(purchaseRepository).getStatisticsByStatus(from, to);
		verify(purchaseRepository).findAllByCreatedAtBetween(from, to, pageable);
	}

	@Test
	void shouldReturnZeroReport_whenNoDataExists() {
		LocalDateTime from = LocalDateTime.of(2026, 2, 1, 0, 0);
		LocalDateTime to = LocalDateTime.of(2026, 2, 28, 23, 59);
		Pageable pageable = PageRequest.of(0, 10);

		when(purchaseRepository.getStatisticsByStatus(from, to)).thenReturn(List.of());
		when(purchaseRepository.findAllByCreatedAtBetween(from, to, pageable)).thenReturn(Page.empty());

		PurchaseReportDto report = purchaseService.getReport(from, to, pageable);

		assertThat(report.totalAmount()).isZero();
		assertThat(report.completedPurchasesCount()).isZero();
		assertThat(report.canceledPurchasesCount()).isZero();
		assertThat(report.transactions().isEmpty()).isTrue();
	}

}
