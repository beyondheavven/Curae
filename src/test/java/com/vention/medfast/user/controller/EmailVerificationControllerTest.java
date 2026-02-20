package com.vention.medfast.user.controller;

import com.vention.medfast.AbstractControllerIntegrationTest;
import com.vention.medfast.email.model.EmailTokenStatus;
import com.vention.medfast.email.model.EmailTokenType;
import com.vention.medfast.email.model.EmailVerificationToken;
import com.vention.medfast.user.model.RoleType;
import com.vention.medfast.user.model.Status;
import com.vention.medfast.user.model.User;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EmailVerificationControllerTest extends AbstractControllerIntegrationTest {

	@Test
	void shouldReturn404_WhenTokenDoesNotExist() throws Exception {
		mockMvc.perform(get("/auth/verify").param("token", "non-existent-uuid")).andExpect(status().isNotFound());
	}

	@Test
	void shouldVerifyEmail_WhenTokenIsValidAndUserIsPatient() throws Exception {
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
		verificationToken.setType(EmailTokenType.VERIFY_EMAIL);
		verificationToken.setExpiresAt(Instant.now().plus(Duration.ofHours(24)));
		em.persist(verificationToken);

		em.flush();
		em.clear();

		mockMvc.perform(get("/auth/verify").param("token", tokenHash)).andExpect(status().isOk());

		User updatedUser = em.find(User.class, patient.getId());
		assertThat(updatedUser.getStatus()).isEqualTo(Status.ACTIVE);

		boolean tokenExists = em
			.createQuery("SELECT count(t) > 0 FROM EmailVerificationToken t WHERE t.tokenHash = :token", Boolean.class)
			.setParameter("token", tokenHash)
			.getSingleResult();
		assertThat(tokenExists).isFalse();
	}

}
