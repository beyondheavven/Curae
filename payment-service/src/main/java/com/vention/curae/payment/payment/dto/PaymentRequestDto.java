package com.vention.curae.payment.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentRequestDto(@NotNull Long serviceId, @NotNull Long patientId, @NotBlank String currency,
		Long appointmentId, String patientEmail) {
}
