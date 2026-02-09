package com.vention.medfast.purchase.controller;

import com.vention.medfast.purchase.dto.PurchaseReportDto;
import com.vention.medfast.purchase.dto.PurchaseResponseDto;
import com.vention.medfast.purchase.service.PurchaseService;
import com.vention.medfast.wrapper.ResponseWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseControllerTest {

	@Mock
	private PurchaseService purchaseService;

	private PurchaseController controller;

	@BeforeEach
	void setUp() {
		controller = new PurchaseController(purchaseService);
	}

	@Test
	void getPurchaseReportReturnsReportWrappedWithOkStatus() {
		LocalDateTime from = LocalDateTime.of(2026, 1, 1, 0, 0);
		LocalDateTime to = LocalDateTime.of(2026, 1, 31, 23, 59);
		Pageable pageable = PageRequest.of(0, 10);

		List<PurchaseResponseDto> content = List.of(new PurchaseResponseDto(1L, 10L, 100L, "pi_123",
				BigDecimal.valueOf(150.0), "usd", "SUCCESS", LocalDateTime.now(), LocalDateTime.now()));
		Page<PurchaseResponseDto> page = new PageImpl<>(content, pageable, 1);

		PurchaseReportDto reportDto = new PurchaseReportDto(150L, 1L, 0L, page);

		when(purchaseService.getReport(from, to, pageable)).thenReturn(reportDto);

		ResponseWrapper<PurchaseReportDto> response = controller.getPurchaseReport(from, to, pageable);

		assertThat(response.getData()).isEqualTo(reportDto);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

		verify(purchaseService).getReport(from, to, pageable);
	}

}
