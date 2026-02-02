package com.vention.medfast.patient.service;

import com.vention.medfast.address.model.Address;
import com.vention.medfast.address.repository.AddressRepository;
import com.vention.medfast.patient.dto.PatientCreateDto;
import com.vention.medfast.patient.dto.PatientResponseDto;
import com.vention.medfast.patient.mapper.PatientMapper;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.repository.PatientRepository;
import com.vention.medfast.registration.dto.PersonDto;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

	private final PatientRepository patientRepository;

	private final AddressRepository addressRepository;

	private final UserRepository userRepository;

	private final PatientMapper patientMapper;

	@Override
	@Transactional
	public PatientResponseDto create(PatientCreateDto patientCreateDto) {
		if (userRepository.existsByEmail(patientCreateDto.user().email())) {
			throw new IllegalArgumentException("User with this email already exists");
		}

		User user = patientMapper.toUser(patientCreateDto);
		User savedUser = userRepository.save(user);

		Address address = patientMapper.toAddress(patientCreateDto.address());
		Address savedAddress = addressRepository.save(address);

		Patient patient = patientMapper.toEntity(patientCreateDto);
		patient.setAddress(savedAddress);
		patient.setUser(savedUser);

		Patient saved = patientRepository.save(patient);

		return patientMapper.toDto(saved);
	}

	@Override
	public PatientResponseDto getPatientById(Long id) {
		return patientMapper.toDto(findById(id));
	}

	@Override
	public Patient getById(Long id) {
		return patientRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Patient not found"));
	}

	@Override
	public Patient getCurrentPatient() {
		User currentUser = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
			.orElseThrow(() -> new EntityNotFoundException("Authenticated user not found"));
		return getPatientByUserId(currentUser.getId());
	}

	@Override
	public Patient getPatientByUserId(Long userId) {
		return patientRepository.findByUserId((userId))
			.orElseThrow(() -> new IllegalArgumentException("Patient profile not found for user with ID: " + userId));
	}

	@Override
	public Patient getPatientByEmail(String email) {
		return patientRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("Patient not found"));
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
