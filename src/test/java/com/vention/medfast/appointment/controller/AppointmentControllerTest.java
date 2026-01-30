package com.vention.medfast.appointment.controller;

import com.vention.medfast.appointment.dto.AppointmentCreateDto;
import com.vention.medfast.appointment.service.AppointmentService;
import com.vention.medfast.wrapper.ResponseWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

	@Mock
	private AppointmentService appointmentService;

	private AppointmentController controller;

	@BeforeEach
	void setUp() {
		controller = new AppointmentController(appointmentService);
	}

	@Test
	void createAppointment_ShouldReturnCreatedId() {
		AppointmentCreateDto dto = new AppointmentCreateDto(null, LocalDate.now().plusDays(1), LocalTime.of(10, 0));

		Long expectedId = 123L;
		when(appointmentService.create(dto)).thenReturn(expectedId);

		ResponseWrapper<Long> response = controller.createAppointment(dto);

		assertThat(response.getData()).isEqualTo(expectedId);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED);
		verify(appointmentService).create(dto);
	}

	@Test
	void createDraftDelegatesToServiceAndReturnsWrapper() {
		AppointmentCreateDto dto = new AppointmentCreateDto(5L, LocalDate.now().plusDays(2), LocalTime.of(9, 30));
		when(appointmentService.create(dto)).thenReturn(77L);

		ResponseWrapper<Long> response = controller.createAppointment(dto);

		assertThat(response.getData()).isEqualTo(77L);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED.value());
		verify(appointmentService).create(dto);
	}

	@Test
	void confirmCallsService() {
		Long appointmentId = 42L;

		controller.confirm(appointmentId);

		verify(appointmentService).confirm(appointmentId);
	}

}
