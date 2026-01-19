package com.vention.medfast.visit.controller;

import com.jayway.jsonpath.JsonPath;
import com.vention.medfast.AbstractControllerIntegrationTest;
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

public class VisitControllerTest extends AbstractControllerIntegrationTest {

	@Test
	void shouldCreateVisit_whenInputIsValid() throws Exception {
		VisitCreateDto dto = new VisitCreateDto(LocalDate.now().plusDays(7), LocalTime.of(10, 30), 1L, 1L, 1L, 1L);

		String response = performRequest(HttpMethod.POST, "/visits", dto).andExpect(status().isCreated())
			.andExpect(jsonPath("$.data").isNumber())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Long createdId = ((Number) JsonPath.read(response, "$.data")).longValue();
		Visit visit = em.find(Visit.class, createdId);

		assertThat(visit).isNotNull();
		assertThat(visit.getId()).isEqualTo(createdId);
		assertThat(visit.getAppointment().getDate()).isEqualTo(dto.scheduledDate());
		assertThat(visit.getAppointment().getTime()).isEqualTo(dto.scheduledTime());

		assertThat(visit.getAppointment().getDoctor().getId()).isEqualTo(dto.doctorId());
		assertThat(visit.getAppointment().getPatient().getId()).isEqualTo(dto.patientId());
		assertThat(visit.getAppointment().getService().getId()).isEqualTo(dto.serviceId());
		assertThat(visit.getClinic().getId()).isEqualTo(dto.clinicId());
	}

	@Test
	void shouldReturn400_whenMultipleFieldsAreInvalid() throws Exception {
		VisitCreateDto invalidDto = new VisitCreateDto(LocalDate.now().minusDays(5), LocalTime.of(10, 0), -1L, 0L, null,
				null);

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
