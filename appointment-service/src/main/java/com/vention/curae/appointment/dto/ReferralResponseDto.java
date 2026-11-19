package com.vention.curae.appointment.dto;

import com.vention.curae.appointment.model.ReferralStatus;
import java.time.LocalDate;

public record ReferralResponseDto(Long id, Long serviceId, LocalDate expiresAt, ReferralStatus status) {
}
