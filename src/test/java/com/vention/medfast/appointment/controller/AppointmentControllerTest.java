package com.vention.medfast.appointment.controller;

import com.vention.medfast.appointment.dto.AppointmentCreateDto;
import com.vention.medfast.appointment.dto.AppointmentCreateWithLabsDto;
import com.vention.medfast.appointment.dto.PatientLabHistoryDTO;
import com.vention.medfast.appointment.service.AppointmentService;
import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.user.model.RoleType;
import com.vention.medfast.wrapper.ResponseWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
        AppointmentCreateDto dto = new AppointmentCreateDto(
            null,
            LocalDate.now().plusDays(1),
            LocalTime.of(10, 0)
        );

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

	@Test
	void getLabHistoryReturnsPageWrappedWithOkStatus() {
		CustomUserDetails user = new CustomUserDetails(3L, "user@mail.com", "pwd", RoleType.PATIENT);
		Pageable pageable = PageRequest.of(0, 10);
		Page<PatientLabHistoryDTO> history = new PageImpl<>(
				List.of(new PatientLabHistoryDTO(1L, LocalDate.now(), "DONE", List.of())));
		when(appointmentService.getPatientLabHistory(user.getId(), pageable)).thenReturn(history);

		ResponseWrapper<Page<PatientLabHistoryDTO>> response = controller.getLabHistory(user, pageable);

		assertThat(response.getData()).isEqualTo(history);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
		verify(appointmentService).getPatientLabHistory(user.getId(), pageable);
	}

	@Test
	void createWithLabsDelegatesToService() {
		AppointmentCreateWithLabsDto request = new AppointmentCreateWithLabsDto(11L, LocalDate.now(),
				LocalTime.of(14, 0), List.of(1L, 2L));

		controller.createWithLabs(request);

		ArgumentCaptor<AppointmentCreateWithLabsDto> captor = ArgumentCaptor
			.forClass(AppointmentCreateWithLabsDto.class);
		verify(appointmentService).createWithLabs(captor.capture());
		assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(request);
	}

}
