package com.vention.medfast.registration.service;

import com.vention.medfast.address.model.Address;
import com.vention.medfast.address.service.AddressService;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.registration.dto.RegistrationRequestDto;
import com.vention.medfast.registration.event.PatientRegisteredEvent;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

	private final UserService userService;

	private final AddressService addressService;

	private final PatientService patientService;

	private final ApplicationEventPublisher eventPublisher;

	@Override
	@Transactional
	public void register(RegistrationRequestDto request) {

		User patientUser = userService.createPatient(request.getPerson());

		Address address = addressService.createAddress(request.getAddress());

		patientService.createPatientProfile(patientUser, address, request.getPerson());

		eventPublisher.publishEvent(new PatientRegisteredEvent(patientUser.getId()));

	}

}
