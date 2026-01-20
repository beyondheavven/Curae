package com.vention.medfast.appointment.controller;

import com.vention.medfast.AbstractControllerIntegrationTest;
import com.vention.medfast.appointment.dto.AppointmentCreateWithLabsDto;
import com.vention.medfast.constants.ApiPaths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AppointmentControllerTest extends AbstractControllerIntegrationTest {

	@Test
	void shouldReturnThelabHistoryForPatient() throws Exception {

        login("john.smith@mail.com", "pass");

        performRequest(HttpMethod.GET, ApiPaths.APPOINTMENT_ENDPOINT + "/lab", null).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.content").isArray())
            .andExpect(jsonPath("$.data.content.length()").value(2))

            .andExpect(jsonPath("$.data.content[*].appointmentId",
                Matchers.containsInAnyOrder(5, 4)))
            .andExpect(jsonPath("$.data.content[*].appointmentStatus",
                Matchers.containsInAnyOrder("SCHEDULED", "COMPLETED")))
            .andExpect(jsonPath("$.data.content[*].date",
                Matchers.everyItem(Matchers.notNullValue())))

            .andExpect(jsonPath("$.data.content[0].tests.length()").value(2))
            .andExpect(jsonPath("$.data.content[0].tests[0].testName").value("Ferritin"))
            .andExpect(jsonPath("$.data.content[0].tests[0].status").value("PROCESSING"))
            .andExpect(jsonPath("$.data.content[0].tests[0].resultUrl").value(Matchers.nullValue()))
            .andExpect(jsonPath("$.data.content[0].tests[0].uploadedAt").value(Matchers.nullValue()))

            .andExpect(jsonPath("$.data.content[1].tests.length()").value(4));

	}

	@Test
	void shouldReturn403ThelabHistoryForPatientMadeByDoctor() throws Exception {

		login("house@med.com", "pass");

		performRequest(HttpMethod.GET, ApiPaths.APPOINTMENT_ENDPOINT + "/lab", null).andDo(print())
			.andExpect(status().isForbidden());

	}

	@Test
	void shouldReturn204NoContentCreateLabTestResultsWhenInputIsValidByDoctor() throws Exception {
		login("house@med.com", "pass");

		AppointmentCreateWithLabsDto request = new AppointmentCreateWithLabsDto(1L, LocalDate.of(3333, 1, 17),
				LocalTime.of(15, 26), List.of(1L, 2L, 5L));

		performRequest(HttpMethod.POST, ApiPaths.APPOINTMENT_ENDPOINT + "/lab", request).andDo(print())
			.andExpect(status().isNoContent());
	}

	@Test
	void shouldReturnBadRequestCreateLabTestResultsWhenInputDateNotValidByDoctor() throws Exception {
		login("house@med.com", "pass");

		AppointmentCreateWithLabsDto request = new AppointmentCreateWithLabsDto(1L, LocalDate.of(1000, 1, 17),
				LocalTime.of(15, 26), List.of(1L, 2L, 5L));

		performRequest(HttpMethod.POST, ApiPaths.APPOINTMENT_ENDPOINT + "/lab", request).andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	void shouldReturnBadRequestCreateLabTestResultsWhenInputPatientNotFoundByDoctor() throws Exception {
		login("house@med.com", "pass");

		AppointmentCreateWithLabsDto request = new AppointmentCreateWithLabsDto(-1L, LocalDate.of(3333, 1, 17),
				LocalTime.of(15, 26), List.of(1L, 2L, 5L));

		performRequest(HttpMethod.POST, ApiPaths.APPOINTMENT_ENDPOINT + "/lab", request).andDo(print())
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldReturnBadRequestCreateLabTestResultsWhenInputLabTestNotFoundByDoctor() throws Exception {
		login("house@med.com", "pass");

		AppointmentCreateWithLabsDto request = new AppointmentCreateWithLabsDto(1L, LocalDate.of(3333, 1, 17),
				LocalTime.of(15, 26), List.of(-1L));

		performRequest(HttpMethod.POST, ApiPaths.APPOINTMENT_ENDPOINT + "/lab", request).andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	void shouldReturn403CreateLabTestResultsWhenInputIsValidByPatient() throws Exception {
		login("john.smith@mail.com", "pass");
		AppointmentCreateWithLabsDto request = new AppointmentCreateWithLabsDto(1L, LocalDate.of(3333, 1, 17),
				LocalTime.of(15, 26), List.of(1L, 2L, 5L));

		performRequest(HttpMethod.POST, ApiPaths.APPOINTMENT_ENDPOINT + "/lab", request).andDo(print())
			.andExpect(status().isForbidden());
	}

}
