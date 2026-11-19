package com.vention.curae.feedback.clinic.service;

import com.vention.curae.feedback.clinic.dto.ClinicFeedbackCreateDto;
import com.vention.curae.feedback.clinic.dto.ClinicFeedbackSummaryDto;
import org.springframework.data.domain.Pageable;

public interface ClinicFeedbackService {

	ClinicFeedbackSummaryDto getFeedbackSummaryByClinicId(Long clinicId, Pageable pageable);

	long create(Long patientId, ClinicFeedbackCreateDto dto);

}