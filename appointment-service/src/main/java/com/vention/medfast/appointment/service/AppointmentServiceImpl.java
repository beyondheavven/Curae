package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.AppointmentCreateDto;
import com.vention.medfast.appointment.dto.AppointmentUpdateDto;
import com.vention.medfast.appointment.event.AppointmentEventPublisher;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.Referral;
import com.vention.medfast.appointment.repository.AppointmentRepository;
import com.vention.medfast.appointment.repository.ReferralRepository;
import com.vention.medfast.appointment.security.AppointmentSecurityService;
import com.vention.medfast.appointment.state.AppointmentStateContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

	private final AppointmentRepository appointmentRepository;

	private final ReferralRepository referralRepository;

	private final AppointmentStateContext stateContext;

	private final AppointmentSecurityService securityService;

	private final AppointmentEventPublisher eventPublisher;

	@Override
	@Transactional
	public Long create(AppointmentCreateDto dto) {
		Referral referral = referralRepository.findById(dto.referralId())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Referral not found"));
		if (!securityService.canUseReferral(referral.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Referral does not belong to current patient");
		}
		if (referral.getStatus() != com.vention.medfast.appointment.model.ReferralStatus.ACTIVE
				|| referral.getExpiresAt().isBefore(dto.date())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Referral is not available");
		}

		Appointment appointment = new Appointment();
		appointment.setPatientId(referral.getPatientId());
		appointment.setReferralId(referral.getId());
		appointment.setType(referral.getAppointmentType());
		appointment.setScheduledDate(dto.date());
		appointment.setScheduledTime(dto.time());
		appointment.setStatus(AppointmentStatus.SCHEDULED);
		Appointment saved = appointmentRepository.save(appointment);
		eventPublisher.appointmentCreated(saved, referral.getIssuedBy(), referral.getServiceId());
		return saved.getId();
	}

	@Override
	@Transactional
	public void confirm(Long appointmentId) {
		if (!securityService.isAppointmentAvailable(appointmentId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Appointment does not belong to current patient");
		}
		Appointment appointment = getById(appointmentId);
		AppointmentStatus oldStatus = appointment.getStatus();
		if (!stateContext.isTransitionAllowed(oldStatus, AppointmentStatus.SCHEDULED_CONFIRMED)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment cannot be confirmed");
		}
		appointment.setStatus(AppointmentStatus.SCHEDULED_CONFIRMED);
		appointmentRepository.save(appointment);
		eventPublisher.appointmentStatusChanged(appointment, doctorId(appointment), oldStatus.name(),
				appointment.getStatus().name());
	}

	@Override
	@Transactional
	public void cancelAppointment(Long appointmentId) {
		if (!securityService.isAppointmentAvailableExtended(appointmentId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to cancel this appointment");
		}
		Appointment appointment = getById(appointmentId);
		AppointmentStatus oldStatus = appointment.getStatus();
		if (stateContext.isTerminal(oldStatus)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment is already finished");
		}
		appointment.setStatus(AppointmentStatus.CANCELLED);
		appointmentRepository.save(appointment);
		eventPublisher.appointmentStatusChanged(appointment, doctorId(appointment), oldStatus.name(),
				appointment.getStatus().name());
	}

	@Override
	@Transactional
	public void update(Long appointmentId, AppointmentUpdateDto dto) {
		if (!securityService.isAppointmentAvailable(appointmentId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Appointment does not belong to current patient");
		}
		Appointment appointment = getById(appointmentId);
		AppointmentStatus oldStatus = appointment.getStatus();
		if (dto.status() != null && !dto.status().equals(oldStatus)
				&& !stateContext.isTransitionAllowed(oldStatus, dto.status())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid appointment status transition");
		}
		if ((dto.date() != null || dto.time() != null) && !stateContext.isEditable(oldStatus)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment is not editable");
		}
		if (dto.date() != null) {
			appointment.setScheduledDate(dto.date());
		}
		if (dto.time() != null) {
			appointment.setScheduledTime(dto.time());
		}
		if (dto.status() != null) {
			appointment.setStatus(dto.status());
		}
		appointmentRepository.save(appointment);
		eventPublisher.appointmentStatusChanged(appointment, doctorId(appointment), oldStatus.name(),
				appointment.getStatus().name());
	}

	@Override
	@Transactional(readOnly = true)
	public Appointment getById(Long appointmentId) {
		return appointmentRepository.findById(appointmentId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByPatientIdDoctorIdAndStatus(Long patientId, Long doctorId, AppointmentStatus status) {
		return appointmentRepository.existsByPatientIdAndDoctorIdAndStatus(patientId, doctorId, status);
	}

	private Long doctorId(Appointment appointment) {
		return appointment.getReferralId() == null ? null
				: referralRepository.findById(appointment.getReferralId()).map(Referral::getIssuedBy).orElse(null);
	}

}
