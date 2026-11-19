package com.vention.curae.registration.service;

import com.vention.curae.registration.dto.DoctorRegistrationRequestDto;
import com.vention.curae.registration.dto.PatientRegistrationRequestDto;
import com.vention.curae.registration.event.DoctorRegisteredEvent;
import com.vention.curae.registration.event.PatientRegisteredEvent;
import com.vention.curae.registration.util.RandomPasswordGenerator;
import com.vention.curae.user.model.User;
import com.vention.curae.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

	private final UserService userService;

	private final ApplicationEventPublisher eventPublisher;

	private final static int TEMPORARY_PASSWORD_LENGTH = 8;

	@Override
	@Transactional
	public void registerPatient(PatientRegistrationRequestDto request) {

		User patientUser = userService.createPatient(request.getPerson());

		eventPublisher
			.publishEvent(new PatientRegisteredEvent(patientUser.getId(), request.getPerson(), request.getAddress()));
	}

	@Override
	@Transactional
	public void registerDoctor(DoctorRegistrationRequestDto request) {
		String tempPassword = RandomPasswordGenerator.generate(TEMPORARY_PASSWORD_LENGTH);

		User user = userService.createDoctor(request, tempPassword);

		eventPublisher.publishEvent(new DoctorRegisteredEvent(user.getId(), tempPassword, request));
	}

}
