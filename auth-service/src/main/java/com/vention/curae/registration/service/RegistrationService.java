package com.vention.curae.registration.service;

import com.vention.curae.registration.dto.DoctorRegistrationRequestDto;
import com.vention.curae.registration.dto.PatientRegistrationRequestDto;

public interface RegistrationService {

	void registerPatient(PatientRegistrationRequestDto request);

	void registerDoctor(DoctorRegistrationRequestDto request);

}
