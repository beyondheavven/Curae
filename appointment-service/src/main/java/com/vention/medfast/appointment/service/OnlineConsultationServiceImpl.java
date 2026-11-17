package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.CreateOnlineConsultationRequest;
import com.vention.medfast.appointment.dto.OnlineConsultationDetails;
import com.vention.medfast.appointment.dto.UpdateOnlineConsultationRequest;
import com.vention.medfast.appointment.event.AppointmentEventPublisher;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.model.OnlineConsultation;
import com.vention.medfast.appointment.repository.AppointmentRepository;
import com.vention.medfast.appointment.repository.OnlineConsultationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class OnlineConsultationServiceImpl implements OnlineConsultationService {

	private final AppointmentRepository appointmentRepository;

	private final OnlineConsultationRepository consultationRepository;

	private final AppointmentEventPublisher eventPublisher;

	@Override
	@Transactional(readOnly = true)
	public OnlineConsultationDetails getDetails(Long consultationId) {
		OnlineConsultation consultation = getConsultation(consultationId);
		Appointment appointment = getAppointment(consultationId);
		return new OnlineConsultationDetails(appointment.getId(), appointment.getPatientId(),
				consultation.getDoctorId(), appointment.getScheduledDate(), appointment.getScheduledTime(),
				appointment.getStatus(), appointment.getType(), appointment.getUpdatedAt(),
				consultation.getMeetingLink());
	}

	@Override
	@Transactional
	public Long create(CreateOnlineConsultationRequest dto) {
		Appointment appointment = new Appointment();
		appointment.setPatientId(dto.patientId());
		appointment.setType(AppointmentType.ONLINE);
		appointment.setStatus(dto.status() == null ? AppointmentStatus.SCHEDULED : dto.status());
		appointment.setScheduledDate(dto.date());
		appointment.setScheduledTime(dto.time());
		Appointment savedAppointment = appointmentRepository.save(appointment);

		OnlineConsultation consultation = new OnlineConsultation();
		consultation.setAppointmentId(savedAppointment.getId());
		consultation.setDoctorId(dto.doctorId());
		consultation.setMeetingLink(dto.meetingLink());
		consultationRepository.save(consultation);
		eventPublisher.appointmentCreated(savedAppointment, dto.doctorId());
		return savedAppointment.getId();
	}

	@Override
	@Transactional
	public void update(UpdateOnlineConsultationRequest dto) {
		OnlineConsultation consultation = getConsultation(dto.onlineConsultationId());
		Appointment appointment = getAppointment(dto.onlineConsultationId());
		AppointmentStatus oldStatus = appointment.getStatus();
		consultation.setDoctorId(dto.doctorId());
		consultation.setMeetingLink(dto.meetingLink());
		appointment.setPatientId(dto.patientId());
		appointment.setScheduledDate(dto.date());
		appointment.setScheduledTime(dto.time());
		if (dto.status() != null) {
			appointment.setStatus(dto.status());
		}
		consultationRepository.save(consultation);
		appointmentRepository.save(appointment);
		eventPublisher.appointmentStatusChanged(appointment, dto.doctorId(), oldStatus.name(),
				appointment.getStatus().name());
	}

	@Override
	@Transactional
	public void deleteById(Long consultationId) {
		getConsultation(consultationId);
		consultationRepository.deleteById(consultationId);
		appointmentRepository.deleteById(consultationId);
	}

	private OnlineConsultation getConsultation(Long consultationId) {
		return consultationRepository.findByAppointmentId(consultationId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Online consultation not found"));
	}

	private Appointment getAppointment(Long appointmentId) {
		return appointmentRepository.findById(appointmentId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));
	}

}
