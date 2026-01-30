package com.vention.medfast.appointment.referral.controller;

import com.vention.medfast.appointment.referral.controller.ReferralController;
import com.vention.medfast.appointment.referral.dto.ReferralCreateDto;
import com.vention.medfast.appointment.referral.dto.ReferralResponseDto;
import com.vention.medfast.appointment.referral.model.Referral;
import com.vention.medfast.appointment.referral.model.ReferralStatus;
import com.vention.medfast.appointment.referral.service.ReferralService;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.wrapper.ResponseWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReferralControllerTest {

	@Mock
	private ReferralService referralService;

	private ReferralController controller;

	@BeforeEach
	void setUp() {
		controller = new ReferralController(referralService);
	}

	@Test
	void createReturnsCreatedWrapperWithId() {
		ReferralCreateDto dto = new ReferralCreateDto(1L, 2L, AppointmentType.VISIT, LocalDate.now().plusDays(5));
		when(referralService.create(dto)).thenReturn(55L);

		ResponseWrapper<Long> response = controller.create(dto);

		assertThat(response.getData()).isEqualTo(55L);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED.value());
		verify(referralService).create(dto);
	}

	@Test
	void getActiveByPatientReturnsListWrappedOk() {
		Long patientId = 3L;
		List<ReferralResponseDto> referrals = List
			.of(new ReferralResponseDto(10L, 20L, LocalDate.now().plusDays(10), ReferralStatus.ACTIVE));
		when(referralService.getActiveByPatient(patientId)).thenReturn(referrals);

		ResponseWrapper<List<ReferralResponseDto>> response = controller.getActiveByPatient(patientId);

		assertThat(response.getData()).isEqualTo(referrals);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
		verify(referralService).getActiveByPatient(patientId);
	}

	@Test
	void markUsedLoadsReferralAndMarksIt() {
		Referral referral = new Referral();
		referral.setId(77L);
		when(referralService.getActiveById(referral.getId())).thenReturn(referral);

		controller.markUsed(referral.getId());

		ArgumentCaptor<Referral> captor = ArgumentCaptor.forClass(Referral.class);
		verify(referralService).getActiveById(referral.getId());
		verify(referralService).markUsed(captor.capture());
		assertThat(captor.getValue()).isSameAs(referral);
	}

}
