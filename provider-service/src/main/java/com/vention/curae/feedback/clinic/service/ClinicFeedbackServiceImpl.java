package com.vention.curae.feedback.clinic.service;

import com.vention.curae.clinic.model.Clinic;
import com.vention.curae.clinic.service.ClinicService;
import com.vention.curae.feedback.clinic.dto.ClinicFeedbackCreateDto;
import com.vention.curae.feedback.clinic.dto.ClinicFeedbackDto;
import com.vention.curae.feedback.clinic.dto.ClinicFeedbackSummaryDto;
import com.vention.curae.feedback.clinic.mapper.ClinicFeedbackMapper;
import com.vention.curae.feedback.clinic.model.ClinicFeedback;
import com.vention.curae.feedback.clinic.repository.ClinicFeedbackRepository;
import com.vention.curae.feedback.exception.FeedbackNotAllowedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClinicFeedbackServiceImpl implements ClinicFeedbackService {

	private final ClinicFeedbackRepository clinicFeedbackRepository;

	private final ClinicFeedbackMapper clinicFeedbackMapper;

	private final ClinicService clinicService;

	@Override
	@Transactional(readOnly = true)
	public ClinicFeedbackSummaryDto getFeedbackSummaryByClinicId(Long clinicId, Pageable pageable) {
		clinicService.findById(clinicId);

		Page<ClinicFeedbackDto> feedbackHistory = clinicFeedbackRepository.findAllByClinicId(clinicId, pageable)
			.map(clinicFeedbackMapper::toDto);

		double avgRating = clinicFeedbackRepository.findAvgRatingByClinicId(clinicId);

		return new ClinicFeedbackSummaryDto(avgRating, feedbackHistory);
	}

	@Override
	@Transactional
	public long create(Long patientId, ClinicFeedbackCreateDto dto) {
		Clinic clinic = clinicService.findById(dto.clinicId());

		boolean feedbackExists = clinicFeedbackRepository.existsByPatientIdAndClinicId(patientId, clinic.getId());
		if (feedbackExists) {
			throw new FeedbackNotAllowedException("Patient has already left feedback for provided clinic");
		}

		ClinicFeedback feedback = clinicFeedbackMapper.toEntity(dto, clinic, patientId);
		return clinicFeedbackRepository.save(feedback).getId();
	}

}