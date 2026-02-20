package com.vention.medfast.password.service;

import com.vention.medfast.api.exception.PasswordResetException;
import com.vention.medfast.email.model.EmailTokenStatus;
import com.vention.medfast.email.model.EmailTokenType;
import com.vention.medfast.email.model.EmailVerificationToken;
import com.vention.medfast.email.service.EmailVerificationTokenService;
import com.vention.medfast.password.event.PasswordResetCreatedEvent;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PasswordResetServiceImplTest {

	@Mock
	private UserService userService;

	@Mock
	private EmailVerificationTokenService tokenService;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@InjectMocks
	private PasswordResetServiceImpl service;

	@Test
	void sendResetCode_shouldCreateNewToken_whenNoExistingToken() {
		User user = new User();
		user.setEmail("test@mail.com");

		EmailVerificationToken token = new EmailVerificationToken();
		token.setCode("1234");
		token.setUser(user);
		token.setType(EmailTokenType.RESET_PASSWORD);

		when(userService.findByUsername("test@mail.com")).thenReturn(user);
		when(tokenService.getLatestResetToken(any(), any())).thenThrow(EntityNotFoundException.class);
		when(tokenService.createToken(any(), any())).thenReturn(token);

		service.sendResetCode("test@mail.com");
		verify(eventPublisher).publishEvent(any(PasswordResetCreatedEvent.class));
	}

	@Test
	void sendResetCode_shouldUpdateExistingToken_whenTokenExists() {
		String email = "test@example.com";
		User user = new User();
		EmailVerificationToken oldToken = new EmailVerificationToken();
		EmailVerificationToken updatedToken = new EmailVerificationToken();
		updatedToken.setCode("1234");

		when(userService.findByUsername(email)).thenReturn(user);
		when(tokenService.getLatestResetToken(email, EmailTokenType.RESET_PASSWORD)).thenReturn(oldToken);
		when(tokenService.updateToken(any(), any())).thenReturn(updatedToken);

		service.sendResetCode(email);

		verify(tokenService).updateToken(any(), any());
		verify(eventPublisher).publishEvent(any(PasswordResetCreatedEvent.class));
	}

	@Test
	void verifyCode_shouldReturnResetToken_whenValid() {
		User user = new User();
		user.setEmail("test@mail.com");

		EmailVerificationToken token = new EmailVerificationToken();
		token.setUser(user);
		token.setCode("1234");
		token.setTokenHash("reset-token");
		token.setExpiresAt(Instant.now().plus(1, ChronoUnit.MINUTES));
		when(tokenService.getLatestResetToken(any(), any())).thenReturn(token);

		String result = service.verifyCode("test@mail.com", "1234");

		assertThat(result).isEqualTo("reset-token");
		assertThat(token.getStatus()).isEqualTo(EmailTokenStatus.VERIFIED);
	}

	@Test
	void verifyCode_shouldThrow_whenCodeIsWrong() {
		User user = new User();

		EmailVerificationToken token = new EmailVerificationToken();
		token.setUser(user);
		token.setCode("1234");
		token.setExpiresAt(Instant.now().plusSeconds(60));
		when(tokenService.getLatestResetToken(any(), any())).thenReturn(token);

		assertThatThrownBy(() -> service.verifyCode("mail", "0000")).isInstanceOf(PasswordResetException.class)
			.hasMessage("Code is wrong");
	}

	@Test
	void verifyCode_shouldThrow_whenCodeExpired() {
		User user = new User();

		EmailVerificationToken token = new EmailVerificationToken();
		token.setUser(user);
		token.setCode("1234");
		token.setExpiresAt(Instant.now().minusSeconds(1));
		when(tokenService.getLatestResetToken(any(), any())).thenReturn(token);

		assertThatThrownBy(() -> service.verifyCode("mail", "1234")).isInstanceOf(PasswordResetException.class)
			.hasMessage("Code is expired");
	}

	@Test
	void sendResetCode_shouldBlockTokenAfterThreeResends() {
		String email = "test@mail.com";

		User user = new User();
		user.setEmail(email);

		EmailVerificationToken existingToken = new EmailVerificationToken();
		existingToken.setResendCount(2);
		existingToken.setExpiresAt(Instant.now().minusSeconds(10));

		EmailVerificationToken updatedToken = new EmailVerificationToken();
		updatedToken.setResendCount(3);
		updatedToken.setBlockedUntil(Instant.now().plusSeconds(1800));

		when(userService.findByUsername(email)).thenReturn(user);
		when(tokenService.getLatestResetToken(email, EmailTokenType.RESET_PASSWORD)).thenReturn(existingToken);
		when(tokenService.updateToken(any(), any())).thenReturn(updatedToken);

		service.sendResetCode(email);

		verify(tokenService).updateToken(any(), any());
		verify(eventPublisher).publishEvent(any(PasswordResetCreatedEvent.class));

		assertThat(updatedToken.getResendCount()).isEqualTo(3);
		assertThat(updatedToken.getBlockedUntil()).isNotNull();
	}

	@Test
	void verifyCode_shouldThrow_whenTokenIsBlocked() {
		User user = new User();

		EmailVerificationToken token = new EmailVerificationToken();
		token.setUser(user);
		token.setCode("1234");
		token.setExpiresAt(Instant.now().plusSeconds(60));
		token.setBlockedUntil(Instant.now().plusSeconds(60));
		when(tokenService.getLatestResetToken(any(), any())).thenReturn(token);

		assertThatThrownBy(() -> service.verifyCode("mail", "1234")).isInstanceOf(PasswordResetException.class)
			.hasMessage("Too many attempts. Try again later");
	}

}
