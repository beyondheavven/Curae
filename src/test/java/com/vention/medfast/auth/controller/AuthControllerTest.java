package com.vention.medfast.auth.controller;

import com.vention.medfast.AbstractControllerIntegrationTest;
import com.vention.medfast.auth.dto.AuthRequestDto;
import com.vention.medfast.auth.dto.AuthResponseDto;
import com.vention.medfast.auth.dto.LogoutRequestDto;
import com.vention.medfast.auth.dto.RefreshTokenRequestDto;
import com.vention.medfast.user.model.RoleType;
import com.vention.medfast.user.model.Status;
import com.vention.medfast.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class AuthControllerTest extends AbstractControllerIntegrationTest {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void shouldReturn401_WhenLoginCredentialsAreInvalid() throws Exception {
		String email = "nonexistinguser@med.com";
		String password = "pass";

		AuthRequestDto loginRequest = new AuthRequestDto(email, password);

		mockMvc
			.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void shouldLoginSuccessfully_WhenCredentialsAreValid() throws Exception {
		AuthRequestDto loginRequest = new AuthRequestDto("jane.doe@mail.com", "pass");

		performRequest(HttpMethod.POST, "/auth/login", loginRequest).andExpect(status().isOk())
			.andExpect(jsonPath("$.data.accessToken").isNotEmpty())
			.andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
	}

	@Test
	void shouldReturn401_WhenRefreshTokenIsMalformed() throws Exception {
		RefreshTokenRequestDto request = new RefreshTokenRequestDto("invalid-uuid-string");

		performRequest(HttpMethod.POST, "/auth/refresh", request).andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.error.errorCode").value("AUTH_ERROR"))
			.andExpect(jsonPath("$.error.message").value("Invalid refresh token"));
	}

	@Test
	void shouldRefreshTokens_whenInputIsValid() throws Exception {
		String uniqueEmail = "refresh-" + UUID.randomUUID() + "@med.com";
		User testUser = new User();
		testUser.setFirstName("John");
		testUser.setLastName("Doe");
		testUser.setBirthDate(LocalDate.of(1990, 9, 8));
		testUser.setEmail(uniqueEmail);
		testUser.setPasswordHash(passwordEncoder.encode("pass"));
		testUser.setStatus(Status.ACTIVE);
		testUser.setRole(RoleType.PATIENT);
		em.persist(testUser);
		em.flush();

		AuthResponseDto initialAuth = authService.login(new AuthRequestDto(uniqueEmail, "pass"));
		String oldRefreshToken = initialAuth.refreshToken();
		Thread.sleep(1001);
		em.clear();

		RefreshTokenRequestDto refreshRequest = new RefreshTokenRequestDto(oldRefreshToken);

		mockMvc
			.perform(post("/auth/refresh").header("Authorization", "Bearer " + initialAuth.accessToken())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(refreshRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
			.andExpect(jsonPath("$.data.accessToken").isNotEmpty());
	}

	@Test
	void shouldReturn404_whenLogoutWithNonExistentToken() throws Exception {
		LogoutRequestDto request = new LogoutRequestDto("");

		performRequest(HttpMethod.POST, "/auth/logout", request).andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.error.errorCode").value("AUTH_ERROR"));
	}

	@Test
	void shouldReturn400_whenLogoutRequestIsInvalid() throws Exception {
		performRequest(HttpMethod.POST, "/auth/logout", null).andExpect(status().isBadRequest());
	}

}
