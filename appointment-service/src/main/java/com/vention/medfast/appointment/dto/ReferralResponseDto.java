package com.vention.medfast.appointment.dto;

import com.vention.medfast.appointment.model.ReferralStatus;
import java.time.LocalDate;

public record ReferralResponseDto(Long id, Long serviceId, LocalDate expiresAt, ReferralStatus status) {
}
