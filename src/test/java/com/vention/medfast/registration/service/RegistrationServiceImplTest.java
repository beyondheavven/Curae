package com.vention.medfast.registration.service;

import com.vention.medfast.address.service.AddressService;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.service.DoctorService;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.registration.dto.DoctorRegistrationRequestDto;
import com.vention.medfast.registration.event.DoctorRegisteredEvent;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static com.vention.medfast.registration.util.DataUtils.getDoctorRegistrationRequestDto;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceImplTest {

	@Mock
	private UserService userService;

	@Mock
	private AddressService addressService;

	@Mock
	private PatientService patientService;

	@Mock
	private DoctorService doctorService;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@InjectMocks
	private RegistrationServiceImpl registrationService;

	@Test
	void shouldRegisterDoctorWhenRequestIsValid() {
		DoctorRegistrationRequestDto request = getDoctorRegistrationRequestDto();

		User mockedUser = new User();
		mockedUser.setId(10L);
		mockedUser.setEmail(request.email());

		Doctor mockedDoctor = new Doctor();

		given(userService.createDoctor(eq(request), anyString())).willReturn(mockedUser);
		given(doctorService.createProfile(mockedUser, request)).willReturn(mockedDoctor);
		registrationService.registerDoctor(request);

		verify(userService).createDoctor(eq(request), anyString());
		verify(doctorService).createProfile(mockedUser, request);
		verify(eventPublisher).publishEvent(any(DoctorRegisteredEvent.class));
	}

	@Test
	void shouldThrowExceptionAndStopFlowWhenUserServiceFails() {
		DoctorRegistrationRequestDto request = getDoctorRegistrationRequestDto();

		given(userService.createDoctor(any(), anyString())).willThrow(new IllegalArgumentException("Email exists"));

		assertThatThrownBy(() -> registrationService.registerDoctor(request))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Email exists");

		verify(doctorService, never()).createProfile(any(), any());
		verify(eventPublisher, never()).publishEvent(any());
	}

}
