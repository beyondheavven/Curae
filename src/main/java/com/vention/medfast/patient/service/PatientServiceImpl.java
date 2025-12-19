package com.vention.medfast.patient.service;

import com.vention.medfast.address.model.Address;
import com.vention.medfast.patient.dto.PatientCreateDto;
import com.vention.medfast.patient.dto.PatientResponseDto;
import com.vention.medfast.patient.mapper.PatientMapper;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.repository.PatientRepository;
import com.vention.medfast.registration.dto.PersonDto;
import com.vention.medfast.user.model.RoleType;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImpl implements PatientService {

	private final PatientRepository patientRepository;

	private final UserRepository userRepository;

	private final PatientMapper patientMapper;

	@Override
	@Transactional
	public PatientResponseDto create(PatientCreateDto patientCreateDto) {
		User user = new User();
		user.setFirstName(patientCreateDto.user().firstName());
		user.setLastName(patientCreateDto.user().lastName());
		user.setProfileUrl(patientCreateDto.user().profileUrl());
		user.setEmail(patientCreateDto.user().email());
		user.setPasswordHash(patientCreateDto.user().password());
		user.setRole(RoleType.PATIENT);

		User savedUser = userRepository.save(user);

		Address address = new Address();
		address.setStreet(patientCreateDto.address().street());
		address.setCity(patientCreateDto.address().city());
		address.setHouseNumber(patientCreateDto.address().houseNumber());
		address.setState(patientCreateDto.address().state());
		address.setApartment(patientCreateDto.address().apartment());
		address.setZipCode(patientCreateDto.address().zipCode());
		address.setPhone(patientCreateDto.address().phone());

		Patient patient = patientMapper.toEntity(patientCreateDto);
		patient.setAddress(address);
		patient.setUser(savedUser);

		Patient saved = patientRepository.save(patient);

		return patientMapper.toDto(saved);
	}

	@Override
	public PatientResponseDto getPatientById(Long id) {
		return patientMapper.toDto(findById(id));
	}

	@Override
	public Patient getPatientByUserId(Long userId) {
		return patientRepository.findByUserId((userId))
			.orElseThrow(() -> new IllegalArgumentException("Patient profile not found for user with ID: " + userId));
	}

	@Override
	public Patient createPatientProfile(User user, Address address, PersonDto personDto) {

		if (patientRepository.existsByUser(user)) {
			throw new IllegalArgumentException("Patient profile already exists for user with ID: " + user.getId());
		}

		Patient patient = patientMapper.toEntity(personDto, user, address);
		return patientRepository.save(patient);
	}

	public Patient findById(Long id) {
		return patientRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Patient not found"));
	}

}
