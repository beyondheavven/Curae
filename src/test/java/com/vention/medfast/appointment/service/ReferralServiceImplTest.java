package com.vention.medfast.appointment.service;

import com.vention.medfast.api.exception.AppointmentException;
import com.vention.medfast.referral.dto.ReferralCreateDto;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.referral.mapper.ReferralMapper;
import com.vention.medfast.referral.model.Referral;
import com.vention.medfast.referral.model.ReferralStatus;
import com.vention.medfast.referral.repository.ReferralRepository;
import com.vention.medfast.referral.service.ReferralServiceImpl;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.service.DoctorService;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
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

	@InjectMocks
	private ReferralServiceImpl referralService;

	@Test
	void shouldCreateReferral_whenInputIsValid() {
		ReferralCreateDto dto = new ReferralCreateDto(1L, 2L, AppointmentType.VISIT, LocalDate.now().plusDays(5));
		Patient patient = new Patient();
		Doctor doctor = new Doctor();
		ProvidedService service = new ProvidedService();
		Referral referral = new Referral();
		referral.setStatus(ReferralStatus.ACTIVE);
		when(patientService.getById(1L)).thenReturn(patient);
		when(doctorService.getCurrentDoctor()).thenReturn(doctor);
		when(providedServiceService.getProvidedServiceById(2L)).thenReturn(service);
		when(referralMapper.toEntity(dto, patient, doctor, service)).thenReturn(referral);
		when(referralRepository.save(referral)).thenReturn(referral);
		Long result = referralService.create(dto);
		assertThat(result).isNull();
		assertThat(referral.getStatus()).isEqualTo(ReferralStatus.ACTIVE);
	}

	@Test
	void shouldMarkReferralAsUsed() {
		Referral referral = new Referral();
		referral.setStatus(ReferralStatus.ACTIVE);
		Referral result = referralService.markUsed(referral);
		assertThat(result.getStatus()).isEqualTo(ReferralStatus.USED);
	}

	@Test
	void shouldThrowException_whenReferralIsExpired() {
		Referral referral = new Referral();
		referral.setStatus(ReferralStatus.ACTIVE);
		referral.setExpiresAt(LocalDate.now().minusDays(1));
		when(referralRepository.findById(1L)).thenReturn(Optional.of(referral));
		assertThatThrownBy(() -> referralService.getActiveById(1L)).isInstanceOf(AppointmentException.class)
			.hasMessageContaining("expired");
	}

}
