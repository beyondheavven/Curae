package com.vention.medfast.chat.dto;

import java.time.Instant;

public record ChatResponseDto(Long id, Long patientId, Long doctorId, Instant createdAt) {
}
