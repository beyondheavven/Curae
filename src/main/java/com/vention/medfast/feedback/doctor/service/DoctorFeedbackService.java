package com.vention.medfast.feedback.doctor.service;

import com.vention.medfast.feedback.doctor.dto.DoctorFeedbackCreateDto;
import com.vention.medfast.feedback.doctor.dto.DoctorFeedbackSummaryDto;
import org.springframework.data.domain.Pageable;

public interface DoctorFeedbackService {

	DoctorFeedbackSummaryDto getFeedbackSummaryByDoctorId(Long doctorId, Pageable pageable);

	long create(String username, DoctorFeedbackCreateDto dto);

}
