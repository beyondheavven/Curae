package com.vention.curae.appointment.dto;

import com.vention.curae.appointment.model.LabResultStatus;
import java.time.LocalDateTime;

public record LabResultDTO(Long id, String testName, LabResultStatus status, String resultUrl,
		LocalDateTime uploadedAt) {
}
