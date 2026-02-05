package com.vention.medfast.purchase.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record PurchaseResponseDto(Long id,

		Long patientId,

		Long serviceId,

		String stripePaymentIntentId,

		BigDecimal amount,

		String currency,

		String status,

                                  LocalDateTime createdAt,

                                  LocalDateTime updatedAt) {
}
