package com.vention.medfast.registration.service;

import com.vention.medfast.address.service.AddressService;
import com.vention.medfast.doctor.dto.DoctorProfileCreateDto;
import com.vention.medfast.doctor.service.DoctorService;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.registration.dto.DoctorRegistrationRequestDto;
import com.vention.medfast.registration.event.DoctorRegisteredEvent;
import com.vention.medfast.registration.mapper.RegistrationMapper;
import com.vention.medfast.registration.util.DataUtils;
import com.vention.medfast.user.dto.UserCreateDto;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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
	private RegistrationMapper mapper;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@InjectMocks
	private RegistrationServiceImpl registrationService;

	@Test
	void shouldRegisterDoctorWhenRequestIsValid() {
		DoctorRegistrationRequestDto request = getDoctorRegistrationRequestDto();

		UserCreateDto userCreateDto = DataUtils.getUserCreateDto();
		DoctorProfileCreateDto doctorProfileCreateDto = DataUtils.getDoctorProfileCreateDto();

		User mockedUser = new User();
		mockedUser.setId(10L);
		mockedUser.setEmail(request.email());

		given(mapper.toUserCreateDto(eq(request), anyString())).willReturn(userCreateDto);
		given(mapper.toDoctorProfileDto(request)).willReturn(doctorProfileCreateDto);
		given(userService.createDoctor(userCreateDto)).willReturn(mockedUser);

		registrationService.registerDoctor(request);

		verify(userService).createDoctor(userCreateDto);
		verify(doctorService).createDoctorProfile(mockedUser, doctorProfileCreateDto);
		verify(eventPublisher).publishEvent(any(DoctorRegisteredEvent.class));
	}

	@Test
	void shouldThrowExceptionAndStopFlowWhenUserServiceFails() {
		DoctorRegistrationRequestDto request = getDoctorRegistrationRequestDto();
		UserCreateDto userCreateDto = DataUtils.getUserCreateDto();

		given(mapper.toUserCreateDto(eq(request), anyString())).willReturn(userCreateDto);
		given(userService.createDoctor(any())).willThrow(new IllegalArgumentException("Email exists"));

		assertThatThrownBy(() -> registrationService.registerDoctor(request))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Email exists");

		verify(doctorService, never()).createDoctorProfile(any(), any());
		verify(eventPublisher, never()).publishEvent(any());
	}

}
