package com.vention.medfast.feedback.clinic.service;

import com.vention.medfast.feedback.clinic.dto.ClinicFeedbackCreateDto;
import com.vention.medfast.feedback.clinic.dto.ClinicFeedbackSummaryDto;
import org.springframework.data.domain.Pageable;

public interface ClinicFeedbackService {

	ClinicFeedbackSummaryDto getFeedbackSummaryByClinicId(Long clinicId, Pageable pageable);

	long create(String username, ClinicFeedbackCreateDto dto);

}
