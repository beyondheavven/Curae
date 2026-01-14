package com.vention.medfast.visit.service;

import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.service.DoctorService;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.service.service.ProvidedServiceService;
import com.vention.medfast.visit.dto.VisitCreateDto;
import com.vention.medfast.visit.mapper.VisitMapper;
import com.vention.medfast.visit.model.Visit;
import com.vention.medfast.visit.repository.VisitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

	private final VisitRepository visitRepository;

	private final VisitMapper visitMapper;

	private final DoctorService doctorService;

	private final PatientService patientService;

	private final ProvidedServiceService providedServiceService;

	@Override
	@Transactional
	public long create(VisitCreateDto dto) {
		Doctor doctor = doctorService.findDoctorById(dto.doctorId());
		Patient patient = patientService.getById(dto.patientId());
		ProvidedService service = providedServiceService.getProvidedServiceById(dto.serviceId());

		Visit visit = visitMapper.toEntity(dto, doctor, patient, service);
		Visit saved = visitRepository.save(visit);
		return saved.getId();
	}

}
