package com.vention.curae.payment.purchase.dto;

import com.vention.curae.payment.purchase.model.PaymentStatus;
import java.math.BigDecimal;

public record StatusStatisticDto(PaymentStatus status, Long count, BigDecimal amount) {
}
