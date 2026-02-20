package com.vention.medfast.password.controller;

import com.vention.medfast.AbstractControllerIntegrationTest;
import com.vention.medfast.email.model.EmailTokenStatus;
import com.vention.medfast.email.model.EmailTokenType;
import com.vention.medfast.email.model.EmailVerificationToken;
import com.vention.medfast.password.dto.ChangePasswordRequestDto;
import com.vention.medfast.password.dto.PasswordResetConfirmDto;
import com.vention.medfast.password.dto.PasswordResetRequestDto;
import com.vention.medfast.password.dto.PasswordResetVerifyDto;
import com.vention.medfast.password.dto.SetPasswordRequestDto;
import com.vention.medfast.password.service.PasswordResetService;
import com.vention.medfast.user.model.RoleType;
import com.vention.medfast.user.model.Status;
import com.vention.medfast.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PasswordControllerTest extends AbstractControllerIntegrationTest {

	@Autowired
	private PasswordResetService passwordResetService;

	@Test
	void shouldChangePassword_WhenCurrentPasswordIsCorrect() throws Exception {
		String oldPassword = "pass";
		String newPassword = "NewPassword123!";

		ChangePasswordRequestDto request = new ChangePasswordRequestDto(oldPassword, newPassword, newPassword);

		performRequest(HttpMethod.POST, "/account/change-password", request).andExpect(status().isOk());
	}

	@Test
	void shouldResetPassword_whenInputIsValid() throws Exception {
		PasswordResetRequestDto request = new PasswordResetRequestDto();
		request.setEmail("admin@med.com");

		performRequest(HttpMethod.POST, "/account/password-reset/request", request).andExpect(status().isOk());
	}

	@Test
	void shouldReturn404_whenCodeIsInvalid() throws Exception {
		PasswordResetVerifyDto verifyDto = new PasswordResetVerifyDto();
		verifyDto.setCode("invalid_code");
		verifyDto.setEmail("admin@med.com");

		performRequest(HttpMethod.POST, "/account/password-reset/verify", verifyDto).andExpect(status().isNotFound());
	}

	@Test
	void shouldVerifyCodeSuccessfully_WhenCodeIsCorrect() throws Exception {
		String email = "john.smith@mail.com";
		passwordResetService.sendResetCode(email);

		String actualCode = em
			.createQuery("SELECT v.code FROM EmailVerificationToken v WHERE v.user.email = :email", String.class)
			.setParameter("email", email)
			.getSingleResult();

		PasswordResetVerifyDto request = new PasswordResetVerifyDto();
		request.setCode(actualCode);
		request.setEmail(email);

		performRequest(HttpMethod.POST, "/account/password-reset/verify", request).andExpect(status().isOk())
			.andExpect(jsonPath("$.data.resetToken").isNotEmpty());
	}

	@Test
	void shouldReturn404_whenTokenIsInvalid() throws Exception {
		String resetToken = "invalid-token";

		PasswordResetConfirmDto confirmDto = new PasswordResetConfirmDto();
		confirmDto.setResetToken(resetToken);
		confirmDto.setNewPassword("NewPassword123!");
		confirmDto.setRepeatPassword("NewPassword123!");

		performRequest(HttpMethod.POST, "/account/password-reset/confirm", confirmDto).andExpect(status().isNotFound());
	}

	@Test
	void shouldSetInitialPassword_whenInputIsValid() throws Exception {
		User patient = new User();
		patient.setFirstName("Patien");
		patient.setLastName("Patien");
		patient.setProfileUrl("test");
		patient.setBirthDate(LocalDate.of(1990, 7, 8));
		patient.setEmail("verify@test.com");
		patient.setPasswordHash("hashed_pass");
		patient.setRole(RoleType.PATIENT);
		patient.setStatus(Status.UNVERIFIED);
		em.persist(patient);

		String tokenHash = "AeeFL5k6fiGdNMKHw1tvROlC8RuFJXQrWqnbH2IY44E";

		EmailVerificationToken verificationToken = new EmailVerificationToken();
		verificationToken.setTokenHash(tokenHash);
		verificationToken.setUser(patient);
		verificationToken.setStatus(EmailTokenStatus.SENT);
		verificationToken.setType(EmailTokenType.SET_PASSWORD);
		verificationToken.setExpiresAt(Instant.now().plus(Duration.ofHours(24)));
		em.persist(verificationToken);

		em.flush();
		em.clear();

		SetPasswordRequestDto request = new SetPasswordRequestDto(tokenHash, "NewSecurePass123!", "NewSecurePass123!");

		performRequest(HttpMethod.POST, "/account/set-password", request).andExpect(status().isNoContent());
	}

}
