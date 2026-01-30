package com.vention.medfast.appointment.service;

import com.vention.medfast.api.exception.AppointmentException;
import com.vention.medfast.appointment.dto.AppointmentCreateDto;
import com.vention.medfast.appointment.dto.AppointmentUpdateDto;
import com.vention.medfast.appointment.mapper.AppointmentMapper;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.repository.AppointmentRepository;
import com.vention.medfast.appointment.state.AppointmentStateContext;
import com.vention.medfast.labresult.mapper.LabResultMapper;
import com.vention.medfast.labresult.service.LabResultService;
import com.vention.medfast.labtest.service.LabTestService;
import com.vention.medfast.notification.eventListener.eventModel.StatusChangedEvent;
import com.vention.medfast.notification.model.NotificationType;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.referral.model.Referral;
import com.vention.medfast.referral.service.ReferralService;
import com.vention.medfast.statistics.dto.response.AppointmentStatusCounts;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

	private final AppointmentRepository appointmentRepository;

	private final AppointmentMapper appointmentMapper;

	private final PatientService patientService;

	private final AppointmentStateContext appointmentStateContext;

	private final ReferralService referralService;

	private final ApplicationEventPublisher eventPublisher;

	@Override
	@Transactional
	public Long create(AppointmentCreateDto dto) {
		Appointment appointment;
		if (dto.referralId() != null) {
			Referral referral = referralService.getActiveById(dto.referralId());
			appointment = appointmentMapper.toAppointment(dto, referral.getPatient(), referral);
			appointment.setType(referral.getAppointmentType());
			appointment.setStatus(AppointmentStatus.SCHEDULED);
		}
		else {
			Patient patient = patientService.getCurrentPatient();
			appointment = appointmentMapper.toAppointment(dto, patient, null);
			appointment.setStatus(AppointmentStatus.SCHEDULED_CONFIRMED);
		}
		return appointmentRepository.save(appointment).getId();
	}

	@Override
	@Transactional
	public void confirm(Long appointmentId) {
		Appointment appointment = findAppointment(appointmentId);
		if (!appointmentStateContext.isTransitionAllowed(appointment.getStatus(),
				AppointmentStatus.SCHEDULED_CONFIRMED)) {
			throw new IllegalStateException("Cannot confirm appointment in status " + appointment.getStatus());
		}
		appointment.setStatus(AppointmentStatus.SCHEDULED_CONFIRMED);
		if (appointment.getReferral() != null) {
			referralService.markUsed(appointment.getReferral());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Appointment getConfirmed(Long appointmentId) {
		Appointment appointment = findAppointment(appointmentId);
		if (appointment.getStatus() != AppointmentStatus.SCHEDULED_CONFIRMED) {
			throw new AppointmentException("Appointment is not confirmed");
		}
		return appointment;
	}

	@Override
	@Transactional(readOnly = true)
	public AppointmentStatusCounts getAllStatusStatistics(LocalDate startDate, LocalDate endDate) {
		return appointmentRepository.getAllStatusStatistics(startDate, endDate);
	}

	@Override
	@Transactional(readOnly = true)
	public AppointmentStatusCounts getStatusStatisticsByType(LocalDate startDate, LocalDate endDate,
			AppointmentType type) {
		return appointmentRepository.getStatusStatisticsByType(startDate, endDate, type);
	}

	@Override
	public Appointment findAppointment(Long appointmentId) {
		return appointmentRepository.findById(appointmentId)
			.orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
	}

	@Transactional
	@Override
	public void cancelAppointment(Long appointmentId) {
		Appointment appointment = findAppointment(appointmentId);
		if (!appointmentStateContext.isTransitionAllowed(appointment.getStatus(), AppointmentStatus.CANCELLED)) {
			throw new IllegalStateException("Cannot cancel appointment in status " + appointment.getStatus());
		}
		appointmentRepository.save(appointment);

		eventPublisher.publishEvent(new StatusChangedEvent(NotificationType.APPOINTMENT_CANCELLED, appointment.getId(),
				appointment.getPatient().getId()));

	}

	@Transactional
	@Override
	public Long update(AppointmentUpdateDto dto, Long id) {
		Appointment appointment = findAppointment(id);

		if (!appointmentStateContext.isEditable(appointment.getStatus())) {
			throw new IllegalStateException("Appointment cannot be edited in status " + appointment.getStatus());
		}
		appointmentMapper.updateFromDto(dto, appointment);

		eventPublisher.publishEvent(new StatusChangedEvent(NotificationType.APPOINTMENT_DETAILS_CHANGED,
				appointment.getId(), appointment.getPatient().getId()));
		return id;
	}

}
