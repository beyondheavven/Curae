package com.vention.medfast.patient.service;

import com.vention.medfast.address.model.Address;
import com.vention.medfast.patient.dto.PatientCreateDto;
import com.vention.medfast.patient.dto.PatientResponseDto;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.registration.dto.PersonDto;
import com.vention.medfast.user.model.User;
import jakarta.validation.Valid;

public interface PatientService {

	PatientResponseDto create(PatientCreateDto patientCreateDto);

	PatientResponseDto getPatientById(Long id);

	Patient getPatientByUserId(Long id);

	Patient createPatientProfile(User patientUser, Address address, @Valid PersonDto person);

}
