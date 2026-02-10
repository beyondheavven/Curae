package com.vention.medfast.feedback.clinic.service;

import com.vention.medfast.api.exception.FeedbackNotAllowedException;
import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.clinic.service.ClinicService;
import com.vention.medfast.feedback.clinic.dto.ClinicFeedbackCreateDto;
import com.vention.medfast.feedback.clinic.dto.ClinicFeedbackDto;
import com.vention.medfast.feedback.clinic.dto.ClinicFeedbackSummaryDto;
import com.vention.medfast.feedback.clinic.mapper.ClinicFeedbackMapper;
import com.vention.medfast.feedback.clinic.model.ClinicFeedback;
import com.vention.medfast.feedback.clinic.repository.ClinicFeedbackRepository;
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
public class ClinicFeedbackServiceImplTest {

	@Mock
	private ClinicFeedbackRepository clinicFeedbackRepository;

	@Mock
	private ClinicFeedbackMapper clinicFeedbackMapper;

	@Mock
	private ClinicService clinicService;

	@Mock
	private PatientService patientService;

	@InjectMocks
	private ClinicFeedbackServiceImpl clinicFeedbackService;

	@Test
	void shouldReturnClinicFeedbackSummary_whenInputIsValid() {
		Clinic clinic = new Clinic();
		clinic.setId(1L);
		Pageable pageable = PageRequest.of(0, 10);

		ClinicFeedback df1 = new ClinicFeedback();
		df1.setRating(4);

		ClinicFeedback df2 = new ClinicFeedback();
		df2.setRating(5);

		ClinicFeedbackDto dto1 = new ClinicFeedbackDto(1L, 1L, 4, "test", LocalDateTime.now());

		ClinicFeedbackDto dto2 = new ClinicFeedbackDto(1L, 1L, 5, "test", LocalDateTime.now());

		List<ClinicFeedback> feedbackList = List.of(df1, df2);
		Page<ClinicFeedback> feedbackPage = new PageImpl<>(feedbackList, pageable, feedbackList.size());

		when(clinicService.findById(1L)).thenReturn(clinic);
		when(clinicFeedbackRepository.findAllByClinicId(1L, pageable)).thenReturn(feedbackPage);
		when(clinicFeedbackRepository.findAvgRatingByClinicId(1L)).thenReturn(4.5);
		when(clinicFeedbackMapper.toDto(df1)).thenReturn(dto1);
		when(clinicFeedbackMapper.toDto(df2)).thenReturn(dto2);

		ClinicFeedbackSummaryDto result = clinicFeedbackService.getFeedbackSummaryByClinicId(1L, pageable);

		assertThat(result.avgRating()).isEqualTo(4.5);
		assertThat(result.history().getTotalElements()).isEqualTo(2);
		assertThat(result.history().getContent()).containsExactlyInAnyOrder(dto1, dto2);
	}

	@Test
	void shouldReturnEmptySummary_whenClinicHasNoFeedbacks() {
		Clinic clinic = new Clinic();
		clinic.setId(1L);
		Pageable pageable = PageRequest.of(0, 10);
		Page<ClinicFeedback> feedbackPage = new PageImpl<>(List.of(), pageable, 0);

		when(clinicService.findById(1L)).thenReturn(clinic);
		when(clinicFeedbackRepository.findAllByClinicId(1L, pageable)).thenReturn(feedbackPage);

		ClinicFeedbackSummaryDto result = clinicFeedbackService.getFeedbackSummaryByClinicId(1L, pageable);

		assertThat(result.avgRating()).isEqualTo(0);
		assertThat(result.history().getTotalElements()).isEqualTo(0);

		verify(clinicService).findById(1L);
		verify(clinicFeedbackRepository).findAllByClinicId(1L, pageable);
	}

	@Test
	void shouldCreateClinicFeedback_whenInputIsValid() {
		String email = "patient@example.com";

		ClinicFeedbackCreateDto dto = new ClinicFeedbackCreateDto(10L, 5, "Great doctor");
		Clinic clinic = new Clinic();
		clinic.setId(10L);
		Patient patient = new Patient();
		patient.setId(5L);

		ClinicFeedback feedbackEntity = new ClinicFeedback();
		feedbackEntity.setId(1L);

		when(patientService.getPatientByEmail(email)).thenReturn(patient);
		when(clinicService.findById(dto.clinicId())).thenReturn(clinic);
		when(clinicFeedbackRepository.existsByPatientIdAndClinicId(patient.getId(), clinic.getId())).thenReturn(false);
		when(clinicFeedbackMapper.toEntity(dto, clinic, patient)).thenReturn(feedbackEntity);
		when(clinicFeedbackRepository.save(feedbackEntity)).thenReturn(feedbackEntity);

		long id = clinicFeedbackService.create(email, dto);
		assertEquals(1L, id);

		verify(clinicFeedbackRepository).save(feedbackEntity);
		verify(patientService).getPatientByEmail(email);
		verify(clinicService).findById(dto.clinicId());
		verify(clinicFeedbackRepository).existsByPatientIdAndClinicId(patient.getId(), clinic.getId());
		verify(clinicFeedbackMapper).toEntity(dto, clinic, patient);
	}

	@Test
	void shouldThrowFeedbackNotAllowedException_whenFeedbackAlreadyExists() {
		String email = "patient@example.com";

		ClinicFeedbackCreateDto dto = new ClinicFeedbackCreateDto(10L, 5, "Great doctor");
		Clinic clinic = new Clinic();
		clinic.setId(10L);
		Patient patient = new Patient();
		patient.setId(5L);

		when(patientService.getPatientByEmail(email)).thenReturn(patient);
		when(clinicService.findById(dto.clinicId())).thenReturn(clinic);
		when(clinicFeedbackRepository.existsByPatientIdAndClinicId(patient.getId(), clinic.getId())).thenReturn(true);

		FeedbackNotAllowedException ex = assertThrows(FeedbackNotAllowedException.class,
				() -> clinicFeedbackService.create(email, dto));
		assertEquals("Patient has already left feedback for provided clinic", ex.getMessage());
	}

}
