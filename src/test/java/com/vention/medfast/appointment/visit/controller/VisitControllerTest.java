package com.vention.medfast.appointment.visit.controller;

import com.jayway.jsonpath.JsonPath;
import com.vention.medfast.AbstractControllerIntegrationTest;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.appointment.dto.VisitCreateDto;
import com.vention.medfast.appointment.model.Visit;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VisitControllerTest extends AbstractControllerIntegrationTest {

	@Test
	void shouldCreateVisit_whenInputIsValid() throws Exception {
		Patient patient = em.find(Patient.class, 1L);
		Doctor doctor = em.find(Doctor.class, 1L);
		ProvidedService service = em.find(ProvidedService.class, 1L);
		Clinic clinic = em.find(Clinic.class, 1L);

		assertThat(patient).as("Patient with ID 1 must exist").isNotNull();
		assertThat(doctor).as("Doctor with ID 1 must exist").isNotNull();
		assertThat(service).as("Service with ID 1 must exist").isNotNull();
		assertThat(clinic).as("Clinic must exist").isNotNull();

		em.clear();

		VisitCreateDto dto = new VisitCreateDto("Diagnosis: Flu", "Symptoms: Fever", "Report: Bed rest", doctor.getId(),
				service.getId(), clinic.getId());

		String url = "/visits?patientId=" + patient.getId();

		String response = performRequest(HttpMethod.POST, url, dto).andExpect(status().isCreated())
			.andExpect(jsonPath("$.data").isNumber())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Long createdId = ((Number) JsonPath.read(response, "$.data")).longValue();

		em.clear();
		Visit visit = em.find(Visit.class, createdId);

		assertThat(visit).isNotNull();
		assertThat(visit.getDiagnosis()).isEqualTo("Diagnosis: Flu");
		assertThat(visit.getSymptoms()).isEqualTo("Symptoms: Fever");
		assertThat(visit.getMedicalReport()).isEqualTo("Report: Bed rest");
		assertThat(visit.getDoctor().getId()).isEqualTo(doctor.getId());
		assertThat(visit.getPatient().getId()).isEqualTo(patient.getId());
		assertThat(visit.getProvidedService().getId()).isEqualTo(service.getId());
		assertThat(visit.getClinic().getId()).isEqualTo(clinic.getId());
		assertThat(visit.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
		assertThat(visit.getDate()).isEqualTo(LocalDate.now());
	}

	@Test
	void shouldReturn400_whenRequiredIdsAreMissing() throws Exception {
		VisitCreateDto invalidDto = new VisitCreateDto("Diag", "Symp", "Rep", null, null, null);

		String url = "/visits?patientId=1";

		performRequest(HttpMethod.POST, url, invalidDto).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.errorCode").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.error.message").exists());
	}

	@Test
	void shouldReturn400_whenPatientIdIsMissing() throws Exception {
		VisitCreateDto dto = new VisitCreateDto("D", "S", "R", 1L, 1L, 1L);

		performRequest(HttpMethod.POST, "/visits", dto).andExpect(status().isBadRequest());
	}

	@Test
	void shouldReturnPastVisitsWhenInputIsValid() throws Exception {
		login("jane.doe@mail.com", "pass");

		Patient patient = em.find(Patient.class, 1L);
		Doctor doctor = em.find(Doctor.class, 1L);
		Clinic clinic = em.find(Clinic.class, 1L);
		ProvidedService service = em.find(ProvidedService.class, 1L);

		Visit pastVisit = new Visit();
		pastVisit.setPatient(patient);
		pastVisit.setDoctor(doctor);
		pastVisit.setClinic(clinic);
		pastVisit.setProvidedService(service);

		pastVisit.setDate(LocalDate.now().minusDays(10));
		pastVisit.setTime(LocalTime.of(10, 0));
		pastVisit.setStatus(AppointmentStatus.COMPLETED);
		pastVisit.setType(AppointmentType.VISIT);

		pastVisit.setDiagnosis("Test Diagnosis");
		pastVisit.setSymptoms("Test Symptoms");
		pastVisit.setMedicalReport("Test Report");

		em.persist(pastVisit);
		em.flush();

		performRequest(HttpMethod.GET, "/visits?type=PAST&page=0&size=10", null).andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content").isArray())
			.andExpect(jsonPath("$.data.content[0].doctor").exists())
			.andExpect(jsonPath("$.data.content[0].clinic").exists())
			.andExpect(jsonPath("$.data.content[0].status").value("COMPLETED"));
	}

	@Test
	void shouldReturnEmptyArrayWhenNoUpcomingVisitsAvailable() throws Exception {
		login("jane.doe@mail.com", "pass");
		performRequest(HttpMethod.GET, "/visits?type=UPCOMING&page=0&size=10", null).andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content").isArray())
			.andExpect(jsonPath("$.data.content.length()").value(0));
	}

	@Test
	void shouldReturn400WhenRequestParamTypeIsInvalid() throws Exception {
		login("jane.doe@mail.com", "pass");
		performRequest(HttpMethod.GET, "/visits?type=INVALID_TYPE", null).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.errorCode").value("BAD_REQUEST"));
	}

	@Test
	void shouldReturn403WhenRequestedVisitsAsAdmin() throws Exception {

		performRequest(HttpMethod.GET, "/visits?type=PAST", null).andExpect(status().isForbidden())
			.andExpect(jsonPath("$.error.errorCode").value("AUTH_ERROR"))
			.andExpect(jsonPath("$.error.message").value("Access denied"));
	}

}
