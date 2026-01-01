package com.vention.medfast.email.service;

import com.vention.medfast.email.mapper.EmailVerificationTokenMapper;
import com.vention.medfast.email.model.EmailTokenType;
import com.vention.medfast.email.model.EmailVerificationToken;
import com.vention.medfast.email.properties.EmailVerificationProperties;
import com.vention.medfast.email.repository.EmailVerificationTokenRepository;
import com.vention.medfast.user.model.RoleType;
import com.vention.medfast.user.model.Status;
import com.vention.medfast.user.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailVerificationTokenServiceImplTest {

	@Mock
	private EmailVerificationTokenRepository tokenRepository;

	@Mock
	private EmailVerificationTokenMapper tokenMapper;

	@Mock
	private EmailVerificationProperties properties;

	@InjectMocks
	private EmailVerificationTokenServiceImpl service;

	@Test
	void shouldGenerateAndSaveTokenWhenValidUserIsPatient() {
		User user = new User();
		user.setId(1L);
		user.setRole(RoleType.PATIENT);

		given(properties.getTtlMs()).willReturn(3600000L);

		EmailVerificationToken mockToken = new EmailVerificationToken();
		mockToken.setId(10L);

		given(tokenMapper.buildToken(eq(user), anyString(), any(Instant.class), eq(EmailTokenType.VERIFY_EMAIL)))
			.willReturn(mockToken);

		String rawToken = service.createForPatient(user);

		assertThat(rawToken).isNotNull();
		verify(tokenRepository).save(mockToken);
	}

	@Test
	void shouldThrowIllegalStateExceptionWhenUserIsNotPatient() {
		User user = new User();
		user.setId(1L);
		user.setRole(RoleType.DOCTOR);

		assertThatThrownBy(() -> service.createForPatient(user)).isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("allowed only for PATIENT");
	}

	@Test
	void shouldThrowIllegalStateExceptionWhenUserIdIsNullWhenCreateForPatient() {
		User user = new User();
		user.setId(null);
		user.setRole(RoleType.PATIENT);

		assertThatThrownBy(() -> service.createForPatient(user)).isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("User must be persisted");
	}

	@Test
	void shouldGenerateAndSaveTokenWhenValidUserIdDoctor() {
		User user = new User();
		user.setId(1L);
		user.setRole(RoleType.DOCTOR);

		given(properties.getTtlMs()).willReturn(3600000L);
		EmailVerificationToken mockToken = new EmailVerificationToken();
		mockToken.setId(10L);
		given(tokenMapper.buildToken(any(), anyString(), any(), eq(EmailTokenType.SET_PASSWORD))).willReturn(mockToken);

		String rawToken = service.createDoctorWelcomeToken(user);

		assertThat(rawToken).isNotNull();
		verify(tokenRepository).save(mockToken);
	}

	@Test
	void shouldThrowIllegalStateExceptionWhenUserIsNotDoctor() {
		User user = new User();
		user.setId(1L);
		user.setRole(RoleType.PATIENT);

		assertThatThrownBy(() -> service.createDoctorWelcomeToken(user)).isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("allowed only for DOCTOR");
	}

	@Test
	void shouldActivateUserAndDeleteTokenWhenTokenIsValidAndUserIsPatient() {
		String rawToken = "validRawToken";
		User user = new User();
		user.setId(1L);
		user.setRole(RoleType.PATIENT);
		user.setStatus(Status.UNVERIFIED);

		EmailVerificationToken token = new EmailVerificationToken();
		token.setUser(user);
		token.setExpiresAt(Instant.now().plusSeconds(100));
		token.setType(EmailTokenType.VERIFY_EMAIL);

		given(tokenRepository.findByTokenHash(anyString())).willReturn(Optional.of(token));

		service.verify(rawToken);

		assertThat(user.getStatus()).isEqualTo(Status.ACTIVE);
		verify(tokenRepository).delete(token);
	}

	@Test
	void shouldThrowIllegalStateExceptionWhenTokenOwnerIsNotPatientInVerify() {
		User user = new User();
		user.setRole(RoleType.DOCTOR);

		EmailVerificationToken token = new EmailVerificationToken();
		token.setUser(user);
		token.setExpiresAt(Instant.now().plusSeconds(60));
		token.setType(EmailTokenType.VERIFY_EMAIL);

		given(tokenRepository.findByTokenHash(anyString())).willReturn(Optional.of(token));

		assertThatThrownBy(() -> service.verify("someToken")).isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("allowed only for PATIENT");

		assertThat(user.getStatus()).isNotEqualTo(Status.ACTIVE);
		verify(tokenRepository, never()).delete(any());
	}

	@Test
	void shouldReturnTokenWhenHashMatchesAndNotExpiredAndTypeMatches() {
		String rawToken = "rawToken";
		EmailVerificationToken token = new EmailVerificationToken();
		token.setExpiresAt(Instant.now().plusSeconds(100)); // Future
		token.setType(EmailTokenType.SET_PASSWORD);

		given(tokenRepository.findByTokenHash(anyString())).willReturn(Optional.of(token));

		EmailVerificationToken result = service.validateToken(rawToken, EmailTokenType.SET_PASSWORD);

		assertThat(result).isEqualTo(token);
	}

	@Test
	void shouldThrowEntityNotFoundExceptionWhenTokenNotFoundInDB() {
		given(tokenRepository.findByTokenHash(anyString())).willReturn(Optional.empty());

		assertThatThrownBy(() -> service.validateToken("missing", EmailTokenType.VERIFY_EMAIL))
			.isInstanceOf(EntityNotFoundException.class)
			.hasMessageContaining("Token not found");
	}

	@Test
	void shouldThrowIllegalStateExceptionWhenTokenIsExpired() {
		EmailVerificationToken token = new EmailVerificationToken();
		token.setExpiresAt(Instant.now().minusSeconds(1));
		token.setType(EmailTokenType.VERIFY_EMAIL);

		given(tokenRepository.findByTokenHash(anyString())).willReturn(Optional.of(token));

		assertThatThrownBy(() -> service.validateToken("expired", EmailTokenType.VERIFY_EMAIL))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("Token expired");
	}

	@Test
	void shouldThrowIllegalStateExceptionWhenTokenTypeDoesNotMatchExpectedType() {
		EmailVerificationToken token = new EmailVerificationToken();
		token.setExpiresAt(Instant.now().plusSeconds(100));
		token.setType(EmailTokenType.VERIFY_EMAIL);

		given(tokenRepository.findByTokenHash(anyString())).willReturn(Optional.of(token));

		assertThatThrownBy(() -> service.validateToken("valid", EmailTokenType.SET_PASSWORD))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("Invalid token type");
	}

}
