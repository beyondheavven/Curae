package com.vention.medfast.payment.purchase.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurchaseResponseDto(Long id, Long patientId, Long serviceId, String stripePaymentIntentId,
		BigDecimal amount, String currency, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
