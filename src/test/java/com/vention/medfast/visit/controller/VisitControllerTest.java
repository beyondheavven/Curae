package com.vention.medfast.visit.controller;

import com.jayway.jsonpath.JsonPath;
import com.vention.medfast.AbstractControllerIntegrationTest;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.visit.dto.VisitCreateDto;
import com.vention.medfast.visit.model.Visit;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class VisitControllerTest extends AbstractControllerIntegrationTest {

	@Test
	void shouldCreateVisit_whenInputIsValid() throws Exception {
		Appointment appointment = new Appointment();
		appointment.setDate(LocalDate.now().plusDays(1));
		appointment.setTime(LocalTime.of(10, 30));
		appointment.setStatus(AppointmentStatus.SCHEDULED_CONFIRMED);
		appointment.setType(AppointmentType.VISIT);
		appointment.setDoctor(em.find(Doctor.class, 1L));
		appointment.setPatient(em.find(Patient.class, 1L));
		em.persist(appointment);
		em.flush();
		VisitCreateDto dto = new VisitCreateDto(appointment.getId(), "Diagnosis", "Symptoms", "Medical report");
		String response = performRequest(HttpMethod.POST, "/visits", dto).andExpect(status().isCreated())
			.andExpect(jsonPath("$.data").isNumber())
			.andReturn()
			.getResponse()
			.getContentAsString();
		Long createdId = ((Number) JsonPath.read(response, "$.data")).longValue();
		Visit visit = em.find(Visit.class, createdId);
		assertThat(visit).isNotNull();
		assertThat(visit.getAppointment().getId()).isEqualTo(appointment.getId());
		assertThat(visit.getDiagnosis()).isEqualTo("Diagnosis");
		assertThat(visit.getSymptoms()).isEqualTo("Symptoms");
		assertThat(visit.getMedicalReport()).isEqualTo("Medical report");
		assertThat(visit.getAppointment().getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
	}

	@Test
	void shouldReturn400_whenMultipleFieldsAreInvalid() throws Exception {
		VisitCreateDto invalidDto = new VisitCreateDto(null, "", "", "");
		performRequest(HttpMethod.POST, "/visits", invalidDto).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.errorCode").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.error.message").exists());
	}

	@Test
	void shouldReturnPastVisitsWhenInputIsValid() throws Exception {
		login("jane.doe@mail.com", "pass");
		performRequest(HttpMethod.GET, "/visits?type=PAST&page=0&size=10", null).andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content").isArray())
			.andExpect(jsonPath("$.data.content.length()").value(1))
			.andExpect(jsonPath("$.data.content[0].doctor.firstName").value("Gregory"))
			.andExpect(jsonPath("$.data.content[0].doctor.lastName").value("House"))
			.andExpect(jsonPath("$.data.content[0].doctor.specializations").isArray())
			.andExpect(jsonPath("$.data.content[0].address.city").value("New York"))
			.andExpect(jsonPath("$.data.content[0].address.street").value("Broadway"))
			.andExpect(jsonPath("$.data.content[0].address.houseNumber").value("100"))
			.andExpect(jsonPath("$.data.content[0].clinic").value("Central City Hospital"))
			.andExpect(jsonPath("$.data.content[0].time").value("10:00:00"))
			.andExpect(jsonPath("$.data.content[0].status").value("COMPLETED"));
	}

	@Test
	void shouldReturnEmptyArrayWhenNoUpcomingVisitsAvailable() throws Exception {
		login("jane.doe@mail.com", "pass");

		performRequest(HttpMethod.GET, "/visits?type=UPCOMING&page=1&size=10", null).andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content").isArray())
			.andExpect(jsonPath("$.data.content.length()").value(0));
	}

	@Test
	void shouldReturn400WhenRequestParamIsInvalid() throws Exception {
		login("jane.doe@mail.com", "pass");
		performRequest(HttpMethod.GET, "/visits?type=LL", null).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.errorCode").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.error.message", Matchers.notNullValue()));
	}

	@Test
	void shouldReturn403WhenRequestedVisitsAsAdmin() throws Exception {
		performRequest(HttpMethod.GET, "/visits?type=PAST", null).andExpect(status().isForbidden())
			.andExpect(jsonPath("$.error.errorCode").value("AUTH_ERROR"))
			.andExpect(jsonPath("$.error.message").value("Access denied"));
	}

}
