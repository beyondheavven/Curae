package com.vention.medfast.appointment.referral.dto;

import com.vention.medfast.appointment.referral.model.ReferralStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "Referral response")
public record ReferralResponseDto(Long id, Long serviceId, LocalDate expiresAt, ReferralStatus status) {
}
