package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.CreateOnlineConsultationRequest;
import com.vention.medfast.appointment.dto.OnlineConsultationDetails;
import com.vention.medfast.appointment.dto.UpdateOnlineConsultationRequest;
import com.vention.medfast.appointment.mapper.OnlineConsultationMapper;
import com.vention.medfast.appointment.model.OnlineConsultation;
import com.vention.medfast.appointment.repository.OnlineConsultationRepository;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.service.DoctorService;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.statistics.dto.response.AppointmentStatusCounts;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class OnlineConsultationServiceImpl implements OnlineConsultationService {

	private final OnlineConsultationRepository onlineConsultationRepository;

	private final OnlineConsultationMapper onlineConsultationMapper;

	private final PatientService patientService;

	private final DoctorService doctorService;

	@Cacheable(cacheNames = "onlineConsultationDetails", key = "'consultation:' + #consultationId", sync = true)
	@Override
	@Transactional(readOnly = true)
	public OnlineConsultationDetails getDetails(Long consultationId) {
		return onlineConsultationRepository.findDetailsById(consultationId)
			.orElseThrow(() -> new EntityNotFoundException("Online consultation not found: " + consultationId));
	}

	@Override
	@Transactional
	public void create(CreateOnlineConsultationRequest createOnlineConsultationRequest) {

		Patient patient = patientService.getById(createOnlineConsultationRequest.patientId());
		Doctor doctor = doctorService.findDoctorById(createOnlineConsultationRequest.doctorId());
		OnlineConsultation oc = onlineConsultationMapper.toEntity(createOnlineConsultationRequest);

		oc.setPatient(patient);
		oc.setDoctor(doctor);

		onlineConsultationRepository.save(oc);
	}

	@CacheEvict(cacheNames = "onlineConsultationDetails", key = "'consultation:' + #consultationId")
	@Override
	@Transactional
	public void deleteById(Long consultationId) {
		OnlineConsultation oc = findById(consultationId);
		onlineConsultationRepository.delete(oc);
	}

	@CacheEvict(cacheNames = "onlineConsultationDetails", key = "'consultation:' + #req.onlineConsultationId()")
	@Override
	@Transactional
	public void update(UpdateOnlineConsultationRequest req) {
		OnlineConsultation oc = findById(req.onlineConsultationId());
		onlineConsultationMapper.updateOnlineConsultation(req, oc);
		if (req.doctorId() != null && !req.doctorId().equals(oc.getDoctor().getId())) {
			Doctor newDoctor = doctorService.findDoctorById(req.doctorId());
			oc.setDoctor(newDoctor);
		}
		onlineConsultationRepository.save(oc);
	}

	@Override
	public AppointmentStatusCounts getStatusStatisticsByClinic(Long clinicId, LocalDate startDate, LocalDate endDate) {
		return onlineConsultationRepository.getStatusStatisticsByClinic(clinicId, startDate, endDate);
	}

	@Override
	public AppointmentStatusCounts getStatusStatisticsByDoctor(Long doctorId, LocalDate start, LocalDate end) {
		return onlineConsultationRepository.getStatusStatisticsByDoctor(doctorId, start, end);
	}

	private OnlineConsultation findById(Long consultationId) {
		return onlineConsultationRepository.findById(consultationId)
			.orElseThrow(() -> new EntityNotFoundException("Online consultation not found: " + consultationId));
	}

}
