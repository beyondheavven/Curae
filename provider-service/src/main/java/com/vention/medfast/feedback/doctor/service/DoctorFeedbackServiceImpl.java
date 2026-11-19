package com.vention.medfast.feedback.doctor.service;

import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.service.DoctorService;
import com.vention.medfast.feedback.client.AppointmentClient;
import com.vention.medfast.feedback.doctor.dto.DoctorFeedbackCreateDto;
import com.vention.medfast.feedback.doctor.dto.DoctorFeedbackDto;
import com.vention.medfast.feedback.doctor.dto.DoctorFeedbackSummaryDto;
import com.vention.medfast.feedback.doctor.mapper.DoctorFeedbackMapper;
import com.vention.medfast.feedback.doctor.model.DoctorFeedback;
import com.vention.medfast.feedback.doctor.repository.DoctorFeedbackRepository;
import com.vention.medfast.feedback.exception.FeedbackNotAllowedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DoctorFeedbackServiceImpl implements DoctorFeedbackService {

	private final DoctorFeedbackRepository doctorFeedbackRepository;

	private final DoctorFeedbackMapper doctorFeedbackMapper;

	private final DoctorService doctorService;

	private final AppointmentClient appointmentClient;

	@Override
	@Transactional(readOnly = true)
	public DoctorFeedbackSummaryDto getFeedbackSummaryByDoctorId(Long doctorId, Pageable pageable) {
		doctorService.findDoctorById(doctorId);

		Page<DoctorFeedbackDto> feedbacksHistory = doctorFeedbackRepository.findAllByDoctorId(doctorId, pageable)
			.map(doctorFeedbackMapper::toDto);

		double avgRating = doctorFeedbackRepository.findAvgRatingByDoctorId(doctorId);

		return new DoctorFeedbackSummaryDto(avgRating, feedbacksHistory);
	}

	@Override
	@Transactional
	public long create(Long patientId, DoctorFeedbackCreateDto dto) {
		Doctor doctor = doctorService.findDoctorById(dto.doctorId());

		boolean completedAppExists = appointmentClient.hasCompletedAppointment(patientId, doctor.getId());
		boolean feedbackExists = doctorFeedbackRepository.existsByPatientIdAndDoctorId(patientId, doctor.getId());

		if (feedbackExists) {
			throw new FeedbackNotAllowedException("Patient has already left feedback for provided doctor");
		}

		if (!completedAppExists) {
			throw new FeedbackNotAllowedException("Patient has not completed appointments with provided doctor");
		}

		DoctorFeedback feedback = doctorFeedbackMapper.toEntity(dto, doctor, patientId);
		return doctorFeedbackRepository.save(feedback).getId();
	}

}