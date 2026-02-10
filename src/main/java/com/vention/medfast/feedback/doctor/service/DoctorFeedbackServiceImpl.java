package com.vention.medfast.feedback.doctor.service;

import com.vention.medfast.api.exception.FeedbackNotAllowedException;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.service.AppointmentService;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.service.DoctorService;
import com.vention.medfast.feedback.doctor.dto.DoctorFeedbackCreateDto;
import com.vention.medfast.feedback.doctor.dto.DoctorFeedbackDto;
import com.vention.medfast.feedback.doctor.dto.DoctorFeedbackSummaryDto;
import com.vention.medfast.feedback.doctor.mapper.DoctorFeedbackMapper;
import com.vention.medfast.feedback.doctor.model.DoctorFeedback;
import com.vention.medfast.feedback.doctor.repository.DoctorFeedbackRepository;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorFeedbackServiceImpl implements DoctorFeedbackService {

	private final DoctorFeedbackRepository doctorFeedbackRepository;

	private final DoctorFeedbackMapper doctorFeedbackMapper;

	private final DoctorService doctorService;

	private final PatientService patientService;

	private final AppointmentService appointmentService;

	@Override
	public DoctorFeedbackSummaryDto getFeedbackSummaryByDoctorId(Long doctorId, Pageable pageable) {
		doctorService.findDoctorById(doctorId);

		Page<DoctorFeedbackDto> feedbacksHistory = doctorFeedbackRepository.findAllByDoctorId(doctorId, pageable)
			.map(doctorFeedbackMapper::toDto);

		double avgRating = doctorFeedbackRepository.findAvgRatingByDoctorId(doctorId);

		return new DoctorFeedbackSummaryDto(avgRating, feedbacksHistory);
	}

	@Override
	public long create(String username, DoctorFeedbackCreateDto dto) {
		Patient patient = patientService.getPatientByEmail(username);
		Doctor doctor = doctorService.findDoctorById(dto.doctorId());

		boolean completedAppExists = appointmentService.existsByPatientIdDoctorIdAndStatus(patient.getId(),
				doctor.getId(), AppointmentStatus.COMPLETED);

		boolean feedbackExists = doctorFeedbackRepository.existsByPatientIdAndDoctorId(patient.getId(), doctor.getId());

		if (feedbackExists) {
			throw new FeedbackNotAllowedException("Patient has already left feedback for provided doctor");
		}

		if (!completedAppExists) {
			throw new FeedbackNotAllowedException("Patient has not completed appointments with provided doctor");
		}
		DoctorFeedback feedback = doctorFeedbackMapper.toEntity(dto, doctor, patient);
		return doctorFeedbackRepository.save(feedback).getId();
	}

}
