package com.vention.medfast.appointment.test.controller;

import com.vention.medfast.appointment.controller.TestController;
import com.vention.medfast.appointment.dto.PatientLabHistoryDTO;
import com.vention.medfast.appointment.dto.TestCreateWithLabsDto;
import com.vention.medfast.appointment.service.TestService;
import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.wrapper.ResponseWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestControllerTest {

	@Mock
	private TestService testService;

	private TestController controller;

	@BeforeEach
	void setUp() {
		controller = new TestController(testService);
	}

	@Test
	void getLabHistory_ShouldReturnPageAndOkStatus() {
		Long userId = 10L;
		CustomUserDetails user = mock(CustomUserDetails.class);
		when(user.getId()).thenReturn(userId);

		Pageable pageable = Pageable.unpaged();
		PatientLabHistoryDTO historyDto = new PatientLabHistoryDTO(1L, LocalDate.now(), "COMPLETED", List.of());
		Page<PatientLabHistoryDTO> expectedPage = new PageImpl<>(List.of(historyDto));

		when(testService.getPatientLabHistory(userId, pageable)).thenReturn(expectedPage);

		ResponseWrapper<Page<PatientLabHistoryDTO>> response = controller.getLabHistory(user, pageable);
		assertThat(response.getData()).isEqualTo(expectedPage);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
		verify(testService).getPatientLabHistory(userId, pageable);
	}

	@Test
	void createWithLabs_ShouldDelegateToService() {
		TestCreateWithLabsDto dto = new TestCreateWithLabsDto(1L, 2L, LocalDate.now().plusDays(1), LocalTime.of(10, 0),
				List.of(100L, 101L));

		controller.createWithLabs(dto);
		verify(testService).createWithLabs(dto);
	}

}
