package com.vention.medfast.auth.registration.service;

import com.vention.medfast.address.model.Address;
import com.vention.medfast.address.service.AddressService;
import com.vention.medfast.doctor.service.DoctorService;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.registration.dto.DoctorRegistrationRequestDto;
import com.vention.medfast.registration.dto.PatientRegistrationRequestDto;
import com.vention.medfast.registration.event.DoctorRegisteredEvent;
import com.vention.medfast.registration.event.PatientRegisteredEvent;
import com.vention.medfast.registration.util.RandomPasswordGenerator;
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

	private final DoctorService doctorService;

	private final ApplicationEventPublisher eventPublisher;

	private final static int TEMPORARY_PASSWORD_LENGTH = 8;

	@Override
	@Transactional
	public void registerPatient(PatientRegistrationRequestDto request) {

		User patientUser = userService.createPatient(request.getPerson());

		Address address = addressService.create(request.getAddress());

		patientService.createPatientProfile(patientUser, address, request.getPerson());

		eventPublisher.publishEvent(new PatientRegisteredEvent(patientUser.getId()));

	}

	@Override
	@Transactional
	public void registerDoctor(DoctorRegistrationRequestDto request) {
		String tempPassword = RandomPasswordGenerator.generate(TEMPORARY_PASSWORD_LENGTH);

		User user = userService.createDoctor(request, tempPassword);

		doctorService.createProfile(user, request);

		eventPublisher.publishEvent(new DoctorRegisteredEvent(user.getId(), tempPassword));
	}

}
