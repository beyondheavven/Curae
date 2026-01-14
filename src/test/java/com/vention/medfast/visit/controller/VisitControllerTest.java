package com.vention.medfast.visit.controller;

import com.jayway.jsonpath.JsonPath;
import com.vention.medfast.AbstractControllerIntegrationTest;
import com.vention.medfast.visit.dto.VisitCreateDto;
import com.vention.medfast.visit.model.Visit;
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
		VisitCreateDto dto = new VisitCreateDto(LocalDate.now().plusDays(7), LocalTime.of(10, 30), 1L, 1L, 1L);

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
	}

	@Test
	void shouldReturn400_whenMultipleFieldsAreInvalid() throws Exception {
		VisitCreateDto invalidDto = new VisitCreateDto(LocalDate.now().minusDays(5), LocalTime.of(10, 0), -1L, 0L,
				null);

		performRequest(HttpMethod.POST, "/visits", invalidDto).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.errorCode").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.error.message").exists());
	}

}
