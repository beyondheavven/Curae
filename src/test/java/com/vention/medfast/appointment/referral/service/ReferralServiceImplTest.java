package com.vention.medfast.appointment.referral.service;

import com.vention.medfast.api.exception.AppointmentException;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.service.DoctorService;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.appointment.referral.dto.ReferralCreateDto;
import com.vention.medfast.appointment.referral.mapper.ReferralMapper;
import com.vention.medfast.appointment.referral.model.Referral;
import com.vention.medfast.appointment.referral.model.ReferralStatus;
import com.vention.medfast.appointment.referral.repository.ReferralRepository;
import com.vention.medfast.appointment.referral.state.ReferralStateContext;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.service.service.ProvidedServiceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReferralServiceImplTest {

	@Mock
	private ReferralRepository referralRepository;

	@Mock
	private ReferralMapper referralMapper;

	@Mock
	private PatientService patientService;

	@Mock
	private DoctorService doctorService;

	@Mock
	private ProvidedServiceService providedServiceService;

	@Mock
	private ReferralStateContext referralStateContext;

	@InjectMocks
	private ReferralServiceImpl referralService;

	@Test
	void shouldCreateReferral_whenInputIsValid() {
		Long expectedReferralId = 10L;
		ReferralCreateDto dto = new ReferralCreateDto(1L, 2L, AppointmentType.VISIT, LocalDate.now().plusDays(5));

		Patient patient = new Patient();
		Doctor doctor = new Doctor();
		ProvidedService service = new ProvidedService();

		Referral referral = new Referral();
		referral.setId(expectedReferralId);
		referral.setStatus(ReferralStatus.ACTIVE);

		when(patientService.getById(1L)).thenReturn(patient);
		when(doctorService.getCurrentDoctor()).thenReturn(doctor);
		when(providedServiceService.getProvidedServiceById(2L)).thenReturn(service);
		when(referralMapper.toEntity(dto, patient, doctor, service)).thenReturn(referral);
		when(referralRepository.save(referral)).thenReturn(referral);

		Long result = referralService.create(dto);

		assertThat(result).isEqualTo(expectedReferralId);
		verify(referralRepository).save(referral);
	}

	@Test
	void shouldMarkReferralAsUsed() {
		Referral referral = new Referral();
		referral.setStatus(ReferralStatus.ACTIVE);

		when(referralStateContext.isTransitionAllowed(ReferralStatus.ACTIVE, ReferralStatus.USED)).thenReturn(true);
		when(referralStateContext.isAvailable(referral)).thenReturn(true);

		Referral result = referralService.markUsed(referral);

		assertThat(result.getStatus()).isEqualTo(ReferralStatus.USED);
		verify(referralStateContext).isTransitionAllowed(ReferralStatus.ACTIVE, ReferralStatus.USED);
		verify(referralStateContext).isAvailable(referral);
	}

	@Test
	void shouldThrowException_whenReferralIsExpired_onGetActiveById() {
		Long referralId = 1L;
		Referral referral = new Referral();
		referral.setStatus(ReferralStatus.ACTIVE);

		when(referralRepository.findById(referralId)).thenReturn(Optional.of(referral));
		when(referralStateContext.isAvailable(referral)).thenReturn(false);

		assertThatThrownBy(() -> referralService.getActiveById(referralId)).isInstanceOf(AppointmentException.class)
			.hasMessageContaining("expired");
	}

	@Test
	void shouldThrowException_whenTransitionNotAllowed_onMarkUsed() {
		Referral referral = new Referral();
		referral.setStatus(ReferralStatus.USED);

		when(referralStateContext.isTransitionAllowed(ReferralStatus.USED, ReferralStatus.USED)).thenReturn(false);

		assertThatThrownBy(() -> referralService.markUsed(referral)).isInstanceOf(AppointmentException.class)
			.hasMessageContaining("Invalid status transition");
	}

}
