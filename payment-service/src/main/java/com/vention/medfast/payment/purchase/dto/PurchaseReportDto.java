package com.vention.medfast.payment.purchase.dto;

import org.springframework.data.domain.Page;

public record PurchaseReportDto(Long totalAmount, Long completedPurchasesCount, Long canceledPurchasesCount,
		Page<PurchaseResponseDto> transactions) {
}
