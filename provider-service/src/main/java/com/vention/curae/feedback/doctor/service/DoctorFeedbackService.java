package com.vention.curae.feedback.doctor.service;

import com.vention.curae.feedback.doctor.dto.DoctorFeedbackCreateDto;
import com.vention.curae.feedback.doctor.dto.DoctorFeedbackSummaryDto;
import org.springframework.data.domain.Pageable;

public interface DoctorFeedbackService {

	DoctorFeedbackSummaryDto getFeedbackSummaryByDoctorId(Long doctorId, Pageable pageable);

	long create(Long patientId, DoctorFeedbackCreateDto dto);

}