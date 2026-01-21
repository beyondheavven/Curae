package com.vention.medfast.appointment.dto;

import com.vention.medfast.labresult.model.LabResultStatus;
import java.time.LocalDateTime;

public record LabResultDTO(Long id, String testName, LabResultStatus status, String resultUrl,
		LocalDateTime uploadedAt) {
}
