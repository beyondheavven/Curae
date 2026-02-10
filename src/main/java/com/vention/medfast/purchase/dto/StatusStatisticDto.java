package com.vention.medfast.purchase.dto;

import com.vention.medfast.purchase.model.PaymentStatus;

import java.math.BigDecimal;

public record StatusStatisticDto(

		PaymentStatus status,

		Long count,

		BigDecimal amount

) {
}
