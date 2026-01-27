package com.vention.medfast.appointment.service;

import com.vention.medfast.api.exception.AppointmentException;
import com.vention.medfast.appointment.dto.AppointmentCreateDto;
import com.vention.medfast.appointment.dto.AppointmentCreateWithLabsDto;
import com.vention.medfast.appointment.dto.PatientLabHistoryDTO;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.referral.model.Referral;
import com.vention.medfast.referral.model.ReferralStatus;
import com.vention.medfast.referral.service.ReferralService;
import com.vention.medfast.appointment.repository.AppointmentRepository;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.service.DoctorService;
import com.vention.medfast.labresult.mapper.LabResultMapper;
import com.vention.medfast.labresult.service.LabResultService;
import com.vention.medfast.labtest.model.LabTest;
import com.vention.medfast.labtest.service.LabTestService;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

	@Mock
	private AppointmentRepository appointmentRepository;

	@Mock
	private ReferralService referralService;

	@Mock
	private DoctorService doctorService;

	@Mock
	private PatientService patientService;

	@Mock
	private AppointmentMapper appointmentMapper;

	@Mock
	private LabTestService labTestService;

	@Mock
	private LabResultService labResultService;

	@Mock
	private LabResultMapper labResultMapper;

	@InjectMocks
	private AppointmentServiceImpl appointmentService;

	@Test
	void shouldCreateDraftWithReferral() {
		AppointmentCreateDto dto = new AppointmentCreateDto(10L, null, LocalDate.now().plusDays(1),
				LocalTime.of(10, 0));
		Referral referral = new Referral();
		referral.setAppointmentType(AppointmentType.VISIT);
		Appointment appointment = new Appointment();
		when(referralService.getActiveById(10L)).thenReturn(referral);
		when(appointmentMapper.toAppointment(dto, referral.getIssuedBy(), referral.getPatient(), referral))
			.thenReturn(appointment);
		when(appointmentRepository.save(appointment)).thenReturn(appointment);
		appointmentService.createDraft(dto);
		assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
		assertThat(appointment.getType()).isEqualTo(AppointmentType.VISIT);
	}

	@Test
	void shouldConfirmAppointmentAndMarkReferralUsed() {
		Referral referral = new Referral();
		referral.setStatus(ReferralStatus.ACTIVE);
		Appointment appointment = new Appointment();
		appointment.setStatus(AppointmentStatus.SCHEDULED);
		appointment.setReferral(referral);
		when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
		appointmentService.confirm(1L);
		assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED_CONFIRMED);
		verify(referralService).markUsed(referral);
	}

	@Test
	void shouldThrowException_whenConfirmingNotDraftAppointment() {
		Appointment appointment = new Appointment();
		appointment.setStatus(AppointmentStatus.COMPLETED);
		when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
		assertThatThrownBy(() -> appointmentService.confirm(1L)).isInstanceOf(AppointmentException.class);
	}

	@Test
	void shouldReturnLabHistoryForUser() {
		Pageable pageable = Pageable.unpaged();
		Appointment appointment = new Appointment();
		appointment.setId(5L);
		PatientLabHistoryDTO dto = new PatientLabHistoryDTO(5L, LocalDate.now(), "COMPLETED", List.of());
		when(appointmentRepository.findLabHistoryPage(7L, pageable)).thenReturn(new PageImpl<>(List.of(appointment)));
		when(labResultMapper.toHistoryDto(appointment)).thenReturn(dto);
		Page<PatientLabHistoryDTO> result = appointmentService.getPatientLabHistory(7L, pageable);
		assertThat(result.getContent()).containsExactly(dto);
	}

	@Test
	void shouldCreateAppointmentWithLabs() {
		AppointmentCreateWithLabsDto req = new AppointmentCreateWithLabsDto(2L, LocalDate.of(2031, 1, 1),
				LocalTime.of(12, 0), List.of(10L, 10L, 11L));
		Patient patient = new Patient();
		Doctor doctor = new Doctor();
		LabTest t1 = new LabTest();
		t1.setId(10L);
		LabTest t2 = new LabTest();
		t2.setId(11L);
		Appointment appointment = new Appointment();
		when(patientService.getById(2L)).thenReturn(patient);
		when(doctorService.getCurrentDoctor()).thenReturn(doctor);
		when(labTestService.findAllById(Set.of(10L, 11L))).thenReturn(List.of(t1, t2));
		when(appointmentMapper.toLabAppointment(req, doctor, patient)).thenReturn(appointment);
		appointmentService.createWithLabs(req);
		verify(appointmentRepository).save(appointment);
		verify(labResultService).createForAppointment(appointment, List.of(t1, t2));
	}

}
