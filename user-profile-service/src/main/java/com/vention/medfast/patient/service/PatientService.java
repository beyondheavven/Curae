package com.vention.medfast.patient.service;

import com.vention.medfast.patient.dto.PatientResponseDto;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientService {

	private final PatientRepository patientRepository;

	@Transactional(readOnly = true)
	public PatientResponseDto getPatientById(Long id) {
		Patient p = patientRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Patient not found"));
		return new PatientResponseDto(p.getId(), p.getUserId(), p.getCitizenship(), p.getGender());
	}

	@Transactional(readOnly = true)
	public PatientResponseDto getByUserId(Long userId) {
		Patient p = patientRepository.findByUserId(userId)
			.orElseThrow(() -> new EntityNotFoundException("Patient profile not found"));
		return new PatientResponseDto(p.getId(), p.getUserId(), p.getCitizenship(), p.getGender());
	}

	@Transactional(readOnly = true)
	public boolean isOwnedByUser(Long patientId, Long userId) {
		return patientRepository.findById(patientId).map(p -> userId.equals(p.getUserId())).orElse(false);
	}

}
