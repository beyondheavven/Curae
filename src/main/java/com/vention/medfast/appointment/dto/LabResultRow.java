package com.vention.medfast.appointment.dto;

import com.vention.medfast.labresult.model.LabResultStatus;

import java.time.LocalDateTime;

public record LabResultRow(Long testId, Long labResultId, String testName, LabResultStatus status, String resultUrl,
		LocalDateTime uploadedAt) {
}
