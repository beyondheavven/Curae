package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.CreateOnlineConsultationRequest;
import com.vention.medfast.appointment.dto.OnlineConsultationDetails;
import com.vention.medfast.appointment.dto.UpdateOnlineConsultationRequest;
import com.vention.medfast.appointment.mapper.OnlineConsultationMapper;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.OnlineConsultation;
import com.vention.medfast.appointment.repository.AppointmentRepository;
import com.vention.medfast.appointment.repository.OnlineConsultationRepository;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.service.DoctorService;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class OnlineConsultationServiceImpl implements OnlineConsultationService {

	private final OnlineConsultationRepository onlineConsultationRepository;

	private final OnlineConsultationMapper onlineConsultationMapper;

	private final PatientService patientService;

	private final DoctorService doctorService;

	private final AppointmentRepository appointmentRepository;

	@Override
	@Transactional(readOnly = true)
	public OnlineConsultationDetails getDetails(Long consultationId) {
		OnlineConsultation consultation = onlineConsultationRepository.findById(consultationId)
			.orElseThrow(() -> new EntityNotFoundException("Consultation not found"));

		return onlineConsultationMapper.toDto(consultation);
	}

	@Override
	@Transactional
	public void create(CreateOnlineConsultationRequest createOnlineConsultationRequest) {

		Patient patient = patientService.getById(createOnlineConsultationRequest.patientId());
		Doctor doctor = doctorService.findDoctorById(createOnlineConsultationRequest.doctorId());

		Appointment appointment = onlineConsultationMapper.toAppointment(createOnlineConsultationRequest);
		appointment.setPatient(patient);
		appointment.setDoctor(doctor);

		OnlineConsultation oc = onlineConsultationMapper.toEntity(createOnlineConsultationRequest);
		oc.setAppointment(appointment);

		onlineConsultationRepository.save(oc);
	}

	@Override
	@Transactional
	public void deleteById(Long consultationId) {

		OnlineConsultation oc = onlineConsultationRepository.findById(consultationId)
			.orElseThrow(() -> new EntityNotFoundException("Online consultation not found: " + consultationId));

		appointmentRepository.delete(oc.getAppointment());
	}

	@Override
	@Transactional
	public void update(UpdateOnlineConsultationRequest req) {

		OnlineConsultation oc = findById(req.onlineConsultationId());
		Appointment appt = oc.getAppointment();
		onlineConsultationMapper.updateOnlineConsultation(req, oc);
		onlineConsultationMapper.updateAppointment(req, appt);
		Doctor doctor = doctorService.findDoctorById(req.doctorId());
		appt.setDoctor(doctor);

	}

	private OnlineConsultation findById(Long consultationId) {
		return onlineConsultationRepository.findById(consultationId)
			.orElseThrow(() -> new EntityNotFoundException("Online consultation not found: " + consultationId));
	}

}
