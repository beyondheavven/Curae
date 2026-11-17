package com.vention.medfast.payment.purchase.dto;

import com.vention.medfast.payment.purchase.model.PaymentStatus;
import java.math.BigDecimal;

public record StatusStatisticDto(PaymentStatus status, Long count, BigDecimal amount) {
}
