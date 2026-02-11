package com.vention.medfast.password.service;

import com.vention.medfast.api.exception.PasswordResetException;
import com.vention.medfast.email.mapper.EmailVerificationTokenMapper;
import com.vention.medfast.email.model.EmailTokenStatus;
import com.vention.medfast.email.model.EmailVerificationToken;
import com.vention.medfast.email.repository.EmailVerificationTokenRepository;
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
import java.util.Optional;

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
	private EmailVerificationTokenRepository tokenRepository;

	@Mock
	private EmailVerificationTokenService tokenService;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@Mock
	private EmailVerificationTokenMapper tokenMapper;

	@InjectMocks
	private PasswordResetServiceImpl service;

	@Test
	void sendResetCode_shouldCreateNewToken_whenNoExistingToken() {
		User user = new User();
		user.setEmail("test@mail.com");

		EmailVerificationToken token = new EmailVerificationToken();
		token.setCode(1234);

		when(userService.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
		when(tokenRepository.findTopByUserEmailAndTypeOrderByCreatedAtDesc(any(), any())).thenReturn(Optional.empty());
		when(tokenMapper.createResetPasswordToken(any(), any(), any(), any())).thenReturn(token);

		service.sendResetCode("test@mail.com");

		verify(tokenRepository).save(token);
		verify(eventPublisher).publishEvent(any(PasswordResetCreatedEvent.class));
	}

	@Test
	void sendResetCode_shouldUpdateExistingToken() {
		User user = new User();
		user.setEmail("test@mail.com");

		EmailVerificationToken existing = new EmailVerificationToken();
		existing.setResendCount(1);
		existing.setExpiresAt(Instant.now().minusSeconds(10));

		when(userService.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
		when(tokenRepository.findTopByUserEmailAndTypeOrderByCreatedAtDesc(any(), any()))
			.thenReturn(Optional.of(existing));

		service.sendResetCode("test@mail.com");

		assertThat(existing.getResendCount()).isEqualTo(2);
		verify(tokenRepository).save(existing);
	}

	@Test
    void sendResetCode_shouldThrow_whenUserNotFound() {
        when(userService.findByEmail("missing@mail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.sendResetCode("missing@mail.com"))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("User not found");
    }

	@Test
	void verifyCode_shouldReturnResetToken_whenValid() {
		User user = new User();
		user.setEmail("test@mail.com");

		EmailVerificationToken token = new EmailVerificationToken();
		token.setUser(user);
		token.setCode(1234);
		token.setExpiresAt(Instant.now().plus(1, ChronoUnit.MINUTES));

		when(userService.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
		when(tokenRepository.findTopByUserEmailAndTypeOrderByCreatedAtDesc(any(), any()))
			.thenReturn(Optional.of(token));
		when(tokenService.createForResetPassword(user)).thenReturn("reset-token");

		String result = service.verifyCode("test@mail.com", "1234");

		assertThat(result).isEqualTo("reset-token");
		assertThat(token.getStatus()).isEqualTo(EmailTokenStatus.VERIFIED);
		verify(tokenRepository).save(token);
	}

	@Test
	void verifyCode_shouldThrow_whenCodeIsWrong() {
		User user = new User();

		EmailVerificationToken token = new EmailVerificationToken();
		token.setUser(user);
		token.setCode(1234);
		token.setExpiresAt(Instant.now().plusSeconds(60));

		when(userService.findByEmail(any())).thenReturn(Optional.of(user));
		when(tokenRepository.findTopByUserEmailAndTypeOrderByCreatedAtDesc(any(), any()))
			.thenReturn(Optional.of(token));

		assertThatThrownBy(() -> service.verifyCode("mail", "0000")).isInstanceOf(PasswordResetException.class)
			.hasMessage("Code is wrong");
	}

	@Test
	void verifyCode_shouldThrow_whenCodeExpired() {
		User user = new User();

		EmailVerificationToken token = new EmailVerificationToken();
		token.setUser(user);
		token.setCode(1234);
		token.setExpiresAt(Instant.now().minusSeconds(1));

		when(userService.findByEmail(any())).thenReturn(Optional.of(user));
		when(tokenRepository.findTopByUserEmailAndTypeOrderByCreatedAtDesc(any(), any()))
			.thenReturn(Optional.of(token));

		assertThatThrownBy(() -> service.verifyCode("mail", "1234")).isInstanceOf(PasswordResetException.class)
			.hasMessage("Code is expired");
	}

	@Test
	void sendResetCode_shouldBlockTokenAfterThreeResends() {
		User user = new User();
		user.setEmail("test@mail.com");

		EmailVerificationToken token = new EmailVerificationToken();
		token.setResendCount(2);
		token.setExpiresAt(Instant.now().minusSeconds(10));

		when(userService.findByEmail(any())).thenReturn(Optional.of(user));
		when(tokenRepository.findTopByUserEmailAndTypeOrderByCreatedAtDesc(any(), any()))
			.thenReturn(Optional.of(token));

		service.sendResetCode("test@mail.com");

		assertThat(token.getResendCount()).isEqualTo(3);
		assertThat(token.getBlockedUntil()).isNotNull();
		verify(tokenRepository).save(token);
	}

	@Test
	void verifyCode_shouldThrow_whenTokenIsBlocked() {
		User user = new User();

		EmailVerificationToken token = new EmailVerificationToken();
		token.setUser(user);
		token.setCode(1234);
		token.setExpiresAt(Instant.now().plusSeconds(60));
		token.setBlockedUntil(Instant.now().plusSeconds(60));

		when(userService.findByEmail(any())).thenReturn(Optional.of(user));
		when(tokenRepository.findTopByUserEmailAndTypeOrderByCreatedAtDesc(any(), any()))
			.thenReturn(Optional.of(token));

		assertThatThrownBy(() -> service.verifyCode("mail", "1234")).isInstanceOf(PasswordResetException.class)
			.hasMessage("Too many attempts. Try again later");
	}

}
