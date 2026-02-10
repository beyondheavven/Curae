package com.vention.medfast.feedback.doctor.controller;

import com.jayway.jsonpath.JsonPath;
import com.vention.medfast.AbstractControllerIntegrationTest;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.referral.model.Referral;
import com.vention.medfast.appointment.referral.model.ReferralStatus;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.feedback.doctor.dto.DoctorFeedbackCreateDto;
import com.vention.medfast.feedback.doctor.model.DoctorFeedback;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.service.model.ProvidedService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DoctorFeedbackControllerTest extends AbstractControllerIntegrationTest {

	@Test
	void shouldReturnDoctorFeedbackSummaryByIdWhenInputIsValid() throws Exception {
		insertValidCompletedAppointment();
		insertValidFeedback();
		performRequest(HttpMethod.GET, "/feedbacks/doctors/{id}", null, 1).andExpect(status().isOk())
			.andExpect(jsonPath("$.data.avgRating").value(4))
			.andExpect(jsonPath("$.data.history.content").isArray())
			.andExpect(jsonPath("$.data.history.numberOfElements").value(1))
			.andExpect(jsonPath("$.data.history.content[0].patientId").value(1))
			.andExpect(jsonPath("$.data.history.content[0].doctorId").value(1))
			.andExpect(jsonPath("$.data.history.content[0].rating").value(4))
			.andExpect(jsonPath("$.data.history.content[0].summary").value("Test summary"));
	}

	@Test
	void shouldReturn404WhenDoctorNotFound() throws Exception {
		performRequest(HttpMethod.GET, "/feedbacks/doctors/{id}", null, 999).andExpect(status().isNotFound())
			.andExpect(jsonPath("$.error.message").value("Doctor not found"));
	}

	@Test
	void shouldCreateDoctorFeedbackWhenInputIsValid() throws Exception {
		login("jane.doe@mail.com", "pass");
		insertValidCompletedAppointment();
		DoctorFeedbackCreateDto dto = new DoctorFeedbackCreateDto(1L, 4, "Test summary");

		String createResponse = performRequest(HttpMethod.POST, "/feedbacks/doctors", dto)
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString();

		long createdId = ((Number) JsonPath.read(createResponse, "$.data")).longValue();

		DoctorFeedback result = em.find(DoctorFeedback.class, createdId);

		assertNotNull(result);
		assertEquals(1, result.getDoctor().getId());
		assertEquals(1, result.getPatient().getId());
		assertEquals(4, result.getRating());
		assertEquals("Test summary", result.getSummary());
	}

	@Test
	void shouldReturn400WhenValidationErrorsPresentWithCreate() throws Exception {
		login("jane.doe@mail.com", "pass");
		DoctorFeedbackCreateDto dto = new DoctorFeedbackCreateDto(1L, 0, "f");

		performRequest(HttpMethod.POST, "/feedbacks/doctors", dto).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.message").value("Validation failed"))
			.andExpect(jsonPath("$.error.validationErrors").isArray())
			.andExpect(jsonPath("$.error.validationErrors", Matchers.not(Matchers.empty())));
	}

	@Test
	void shouldReturn403WhenRequestedCreateNotAsPatient() throws Exception {
		DoctorFeedbackCreateDto dto = new DoctorFeedbackCreateDto(1L, 4, "feedback");

		performRequest(HttpMethod.POST, "/feedbacks/doctors", dto).andExpect(status().isForbidden());
	}

	@Test
	void shouldReturn400WhenNoCompletedAppointments() throws Exception {
		login("jane.doe@mail.com", "pass");
		DoctorFeedbackCreateDto dto = new DoctorFeedbackCreateDto(1L, 4, "Test feedback");

		performRequest(HttpMethod.POST, "/feedbacks/doctors", dto).andExpect(status().isBadRequest())
			.andExpect(
					jsonPath("$.error.message").value("Patient has not completed appointments with provided doctor"));
	}

	@Test
	void shouldReturn400WhenMoreThanOneFeedback() throws Exception {
		insertValidCompletedAppointment();
		insertValidFeedback();
		login("jane.doe@mail.com", "pass");
		DoctorFeedbackCreateDto dto = new DoctorFeedbackCreateDto(1L, 4, "Test feedback");

		performRequest(HttpMethod.POST, "/feedbacks/doctors", dto).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.message").value("Patient has already left feedback for provided doctor"));
	}

	private void insertValidCompletedAppointment() {
		Patient p = em.find(Patient.class, 1);
		Doctor doctor = em.find(Doctor.class, 1);
		ProvidedService providedService = em.find(ProvidedService.class, 1);

		Referral ref = new Referral();
		ref.setPatient(p);
		ref.setDoctor(doctor);
		ref.setService(providedService);
		ref.setAppointmentType(AppointmentType.VISIT);
		ref.setExpiresAt(LocalDate.of(2040, 2, 10));
		ref.setStatus(ReferralStatus.ACTIVE);

		em.persist(ref);

		Appointment appointment = new Appointment();
		appointment.setDate(LocalDate.now());
		appointment.setTime(LocalTime.now());
		appointment.setStatus(AppointmentStatus.COMPLETED);
		appointment.setReferral(ref);
		appointment.setPatient(p);
		appointment.setType(AppointmentType.VISIT);

		em.persist(appointment);
	}

	private void insertValidFeedback() {
		Patient p = em.find(Patient.class, 1);
		Doctor doctor = em.find(Doctor.class, 1);

		DoctorFeedback doctorFeedback = new DoctorFeedback();
		doctorFeedback.setPatient(p);
		doctorFeedback.setDoctor(doctor);
		doctorFeedback.setRating(4);
		doctorFeedback.setSummary("Test summary");

		em.persist(doctorFeedback);
	}

}
