package com.vention.medfast.patient.service;

import com.vention.medfast.address.model.Address;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.registration.dto.PersonDto;
import com.vention.medfast.user.model.User;

public interface PatientService {

	Patient createPatientProfile(User user, Address address, PersonDto personDto);

	Patient getPatientByUserId(Long userId);

}
