package com.vention.medfast.feedback.doctor.service;

import com.vention.medfast.api.exception.FeedbackNotAllowedException;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.service.AppointmentService;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.service.DoctorService;
import com.vention.medfast.feedback.doctor.dto.DoctorFeedbackCreateDto;
import com.vention.medfast.feedback.doctor.dto.DoctorFeedbackDto;
import com.vention.medfast.feedback.doctor.dto.DoctorFeedbackSummaryDto;
import com.vention.medfast.feedback.doctor.mapper.DoctorFeedbackMapper;
import com.vention.medfast.feedback.doctor.model.DoctorFeedback;
import com.vention.medfast.feedback.doctor.repository.DoctorFeedbackRepository;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DoctorFeedbackServiceImplTest {

	@Mock
	private DoctorFeedbackRepository doctorFeedbackRepository;

	@Mock
	private DoctorFeedbackMapper doctorFeedbackMapper;

	@Mock
	private DoctorService doctorService;

	@Mock
	private PatientService patientService;

	@Mock
	private AppointmentService appointmentService;

	@InjectMocks
	private DoctorFeedbackServiceImpl doctorFeedbackService;

	@Test
	void shouldReturnDoctorFeedbackSummary_whenInputIsValid() {
		Long doctorId = 1L;
		Pageable pageable = PageRequest.of(0, 10);

		Doctor doctor = new Doctor();
		doctor.setId(doctorId);

		DoctorFeedback df1 = new DoctorFeedback();
		df1.setRating(4);

		DoctorFeedback df2 = new DoctorFeedback();
		df2.setRating(5);

		List<DoctorFeedback> feedbackList = List.of(df1, df2);
		Page<DoctorFeedback> feedbackPage = new PageImpl<>(feedbackList, pageable, feedbackList.size());

		DoctorFeedbackDto dto1 = new DoctorFeedbackDto(1L, doctorId, 4, "test", LocalDateTime.now());

		DoctorFeedbackDto dto2 = new DoctorFeedbackDto(1L, doctorId, 5, "test", LocalDateTime.now());

		when(doctorService.findDoctorById(doctorId)).thenReturn(doctor);
		when(doctorFeedbackRepository.findAllByDoctorId(doctorId, pageable)).thenReturn(feedbackPage);
		when(doctorFeedbackRepository.findAvgRatingByDoctorId(1L)).thenReturn(4.5);
		when(doctorFeedbackMapper.toDto(df1)).thenReturn(dto1);
		when(doctorFeedbackMapper.toDto(df2)).thenReturn(dto2);

		DoctorFeedbackSummaryDto result = doctorFeedbackService.getFeedbackSummaryByDoctorId(doctorId, pageable);

		assertThat(result.avgRating()).isEqualTo(4.5);
		assertThat(result.history().getTotalElements()).isEqualTo(2);
		assertThat(result.history().getContent()).containsExactlyInAnyOrder(dto1, dto2);
	}

	@Test
	void shouldReturnEmptySummary_whenDoctorHasNoFeedbacks() {
		Doctor doctor = new Doctor();
		doctor.setId(1L);
		Pageable pageable = PageRequest.of(0, 10);
		Page<DoctorFeedback> feedbackPage = new PageImpl<>(List.of(), pageable, 0);

		when(doctorService.findDoctorById(1L)).thenReturn(doctor);
		when(doctorFeedbackRepository.findAllByDoctorId(1L, pageable)).thenReturn(feedbackPage);

		DoctorFeedbackSummaryDto result = doctorFeedbackService.getFeedbackSummaryByDoctorId(1L, pageable);

		assertThat(result.avgRating()).isEqualTo(0);
		assertThat(result.history().getTotalElements()).isEqualTo(0);

		verify(doctorService).findDoctorById(1L);
		verify(doctorFeedbackRepository).findAllByDoctorId(1L, pageable);
	}

	@Test
	void shouldCreateDoctorFeedback_whenInputIsValid() {
		String email = "patient@example.com";

		DoctorFeedbackCreateDto dto = new DoctorFeedbackCreateDto(10L, 5, "Great doctor");
		Doctor doctor = new Doctor();
		doctor.setId(10L);
		Patient patient = new Patient();
		patient.setId(5L);

		DoctorFeedback feedbackEntity = new DoctorFeedback();
		feedbackEntity.setId(1L);

		when(patientService.getPatientByEmail(email)).thenReturn(patient);
		when(doctorService.findDoctorById(dto.doctorId())).thenReturn(doctor);
		when(appointmentService.existsByPatientIdDoctorIdAndStatus(patient.getId(), doctor.getId(),
				AppointmentStatus.COMPLETED))
			.thenReturn(true);
		when(doctorFeedbackRepository.existsByPatientIdAndDoctorId(patient.getId(), doctor.getId())).thenReturn(false);
		when(doctorFeedbackMapper.toEntity(dto, doctor, patient)).thenReturn(feedbackEntity);
		when(doctorFeedbackRepository.save(feedbackEntity)).thenReturn(feedbackEntity);

		long id = doctorFeedbackService.create(email, dto);
		assertEquals(1L, id);

		verify(doctorFeedbackRepository).save(feedbackEntity);
		verify(patientService).getPatientByEmail(email);
		verify(doctorService).findDoctorById(dto.doctorId());
		verify(doctorFeedbackRepository).existsByPatientIdAndDoctorId(patient.getId(), doctor.getId());
		verify(doctorFeedbackMapper).toEntity(dto, doctor, patient);
	}

	@Test
	void shouldThrowFeedbackNotAllowedException_whenFeedbackAlreadyExists() {
		String email = "patient@example.com";

		DoctorFeedbackCreateDto dto = new DoctorFeedbackCreateDto(10L, 5, "Great doctor");
		Doctor doctor = new Doctor();
		doctor.setId(10L);
		Patient patient = new Patient();
		patient.setId(5L);

		when(patientService.getPatientByEmail(email)).thenReturn(patient);
		when(doctorService.findDoctorById(dto.doctorId())).thenReturn(doctor);
		when(doctorFeedbackRepository.existsByPatientIdAndDoctorId(patient.getId(), doctor.getId())).thenReturn(true);

		FeedbackNotAllowedException ex = assertThrows(FeedbackNotAllowedException.class,
				() -> doctorFeedbackService.create(email, dto));
		assertEquals("Patient has already left feedback for provided doctor", ex.getMessage());
	}

	@Test
	void shouldThrowFeedbackNotAllowedException_whenNoCompletedAppointments() {
		String email = "patient@example.com";

		DoctorFeedbackCreateDto dto = new DoctorFeedbackCreateDto(10L, 5, "Great doctor");
		Doctor doctor = new Doctor();
		doctor.setId(10L);
		Patient patient = new Patient();
		patient.setId(5L);

		when(patientService.getPatientByEmail(email)).thenReturn(patient);
		when(doctorService.findDoctorById(dto.doctorId())).thenReturn(doctor);
		when(doctorFeedbackRepository.existsByPatientIdAndDoctorId(patient.getId(), doctor.getId())).thenReturn(false);
		when(appointmentService.existsByPatientIdDoctorIdAndStatus(patient.getId(), doctor.getId(),
				AppointmentStatus.COMPLETED))
			.thenReturn(false);

		FeedbackNotAllowedException ex = assertThrows(FeedbackNotAllowedException.class,
				() -> doctorFeedbackService.create(email, dto));
		assertEquals("Patient has not completed appointments with provided doctor", ex.getMessage());
	}

}
