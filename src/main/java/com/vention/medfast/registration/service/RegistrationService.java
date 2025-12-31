package com.vention.medfast.registration.service;

import com.vention.medfast.registration.dto.DoctorRegistrationRequestDto;
import com.vention.medfast.registration.dto.PatientRegistrationRequestDto;

public interface RegistrationService {

	void registerPatient(PatientRegistrationRequestDto request);

	void registerDoctor(DoctorRegistrationRequestDto request);

}
