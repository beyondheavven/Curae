package com.vention.medfast.appointment.onlineConsultation.controller;

import com.vention.medfast.AbstractControllerIntegrationTest;
import com.vention.medfast.appointment.dto.UpdateOnlineConsultationRequest;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.OnlineConsultation;
import com.vention.medfast.auth.dto.AuthRequestDto;
import com.vention.medfast.constants.ApiPaths;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OnlineConsultationControllerIntegrationTest extends AbstractControllerIntegrationTest {

	private static final Long EXISTING_ID = 3L; // seeded in V4__seed_data.sql

	@Test
	void shouldReturnOnlineConsultationWhenAuthorized() throws Exception {
		performRequest(HttpMethod.GET, ApiPaths.ONLINE_CONSULTATION_ENDPOINT + "/{id}", null, EXISTING_ID)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(EXISTING_ID))
			.andExpect(jsonPath("$.data.meetingLink").value("https://zoom.us/j/987654321"));
	}

	@Test
	void shouldForbidWhenNoToken() throws Exception {
		mockMvc.perform(get(ApiPaths.ONLINE_CONSULTATION_ENDPOINT + "/{id}", EXISTING_ID))
			.andExpect(status().isForbidden());
	}

	@Test
	void shouldReturnUnauthorizedWhenTokenInvalid() throws Exception {
		mockMvc
			.perform(get(ApiPaths.ONLINE_CONSULTATION_ENDPOINT + "/{id}", EXISTING_ID).header("Authorization",
					"Bearer invalid-token"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void shouldUpdateOnlineConsultation() throws Exception {
		UpdateOnlineConsultationRequest req = new UpdateOnlineConsultationRequest(EXISTING_ID, 2L, 1L,
				LocalDate.now().plusDays(5), LocalTime.of(15, 0), AppointmentStatus.IN_CONSULTATION,
				"https://zoom.us/j/updated");

		String token = authService.login(new AuthRequestDto("watson@med.com", "pass")).getAccessToken();

		mockMvc
			.perform(patch(ApiPaths.ONLINE_CONSULTATION_ENDPOINT).header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
			.andExpect(status().isNoContent());

		em.flush();
		em.clear();

		OnlineConsultation updated = em.find(OnlineConsultation.class, EXISTING_ID);
		assertThat(updated).isNotNull();
		assertThat(updated.getMeetingLink()).isEqualTo("https://zoom.us/j/updated");
		assertThat(updated.getDoctor().getId()).isEqualTo(1L);
		assertThat(updated.getDate()).isEqualTo(req.date());
		assertThat(updated.getTime()).isEqualTo(req.time());
		assertThat(updated.getStatus()).isEqualTo(AppointmentStatus.IN_CONSULTATION);
		assertThat(updated.getType().name()).isEqualTo("ONLINE");
	}

	@Test
	void shouldDelete() throws Exception {
		performRequest(HttpMethod.DELETE, ApiPaths.ONLINE_CONSULTATION_ENDPOINT + "/{id}", null, EXISTING_ID)
			.andExpect(status().isNoContent());

		em.flush();
		em.clear();

		OnlineConsultation deleted = em.find(OnlineConsultation.class, EXISTING_ID);
		assertThat(deleted).isNull();
	}

}
