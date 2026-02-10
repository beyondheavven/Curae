package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.VisitListItemDto;
import com.vention.medfast.appointment.service.cache.VisitCacheService;
import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.clinic.service.ClinicService;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.service.DoctorService;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.service.service.ProvidedServiceService;
import com.vention.medfast.appointment.dto.VisitCreateDto;
import com.vention.medfast.appointment.mapper.VisitMapper;
import com.vention.medfast.appointment.model.Visit;
import com.vention.medfast.appointment.model.VisitType;
import com.vention.medfast.appointment.repository.VisitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

	private final VisitRepository visitRepository;

	private final VisitMapper visitMapper;

	private final PatientService patientService;

	private final DoctorService doctorService;

	private final ProvidedServiceService providedServiceService;

	private final ClinicService clinicService;

	private final VisitCacheService visitCacheService;

	@Override
	@Transactional
	@CacheEvict(cacheNames = "patientVisits", allEntries = true)
	public Long create(Long patientId, VisitCreateDto dto) {
		Patient patient = patientService.getById(patientId);

		Doctor doctor = doctorService.findDoctorById(dto.doctorId());

		ProvidedService service = providedServiceService.getProvidedServiceById(dto.serviceId());

		Clinic clinic = clinicService.findById(dto.clinicId());

		Visit visit = visitMapper.toEntity(dto, patient, doctor, service, clinic);

		return visitRepository.save(visit).getId();
	}

	@Override
	public Slice<VisitListItemDto> findAllForPatient(VisitType type, Pageable pageable) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		Patient patient = patientService.getPatientByEmail(email);

		return visitCacheService.getVisits(patient.getId(), type, pageable.getPageNumber(), pageable.getPageSize());
	}

}
