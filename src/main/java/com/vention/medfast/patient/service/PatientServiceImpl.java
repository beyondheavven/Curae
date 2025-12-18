package com.vention.medfast.patient.service;

import com.vention.medfast.address.model.Address;
import com.vention.medfast.patient.mapper.PatientMapper;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.repository.PatientRepository;
import com.vention.medfast.registration.dto.PersonDto;
import com.vention.medfast.user.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PatientServiceImpl implements PatientService {

	private final PatientRepository patientRepository;

	private final PatientMapper patientMapper;

	@Override
	public Patient createPatientProfile(User user, Address address, PersonDto personDto) {

		if (patientRepository.existsByUser(user)) {
			throw new IllegalArgumentException("Patient profile already exists for user with ID: " + user.getId());
		}

		Patient patient = patientMapper.toEntity(personDto, user, address);
		return patientRepository.save(patient);
	}

	@Override
	public Patient getPatientByUserId(Long userId) {
		return patientRepository.findByUserId(userId)
			.orElseThrow(() -> new IllegalArgumentException("Patient profile not found for user with ID: " + userId));
	}

}
