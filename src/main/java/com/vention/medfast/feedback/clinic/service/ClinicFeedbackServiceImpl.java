package com.vention.medfast.feedback.clinic.service;

import com.vention.medfast.api.exception.FeedbackNotAllowedException;
import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.clinic.service.ClinicService;
import com.vention.medfast.feedback.clinic.dto.ClinicFeedbackCreateDto;
import com.vention.medfast.feedback.clinic.dto.ClinicFeedbackDto;
import com.vention.medfast.feedback.clinic.dto.ClinicFeedbackSummaryDto;
import com.vention.medfast.feedback.clinic.mapper.ClinicFeedbackMapper;
import com.vention.medfast.feedback.clinic.model.ClinicFeedback;
import com.vention.medfast.feedback.clinic.repository.ClinicFeedbackRepository;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClinicFeedbackServiceImpl implements ClinicFeedbackService {

	private final ClinicFeedbackRepository clinicFeedbackRepository;

	private final ClinicFeedbackMapper clinicFeedbackMapper;

	private final ClinicService clinicService;

	private final PatientService patientService;

	@Override
	public ClinicFeedbackSummaryDto getFeedbackSummaryByClinicId(Long clinicId, Pageable pageable) {
		clinicService.findById(clinicId);

		Page<ClinicFeedbackDto> feedbackHistory = clinicFeedbackRepository.findAllByClinicId(clinicId, pageable)
			.map(clinicFeedbackMapper::toDto);

		double avgRating = clinicFeedbackRepository.findAvgRatingByClinicId(clinicId);

		return new ClinicFeedbackSummaryDto(avgRating, feedbackHistory);
	}

	@Override
	public long create(String username, ClinicFeedbackCreateDto dto) {
		Patient patient = patientService.getPatientByEmail(username);
		Clinic clinic = clinicService.findById(dto.clinicId());

		boolean feedbackExists = clinicFeedbackRepository.existsByPatientIdAndClinicId(patient.getId(), clinic.getId());

		if (feedbackExists) {
			throw new FeedbackNotAllowedException("Patient has already left feedback for provided clinic");
		}

		ClinicFeedback feedback = clinicFeedbackMapper.toEntity(dto, clinic, patient);
		return clinicFeedbackRepository.save(feedback).getId();
	}

}
