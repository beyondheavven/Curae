package com.vention.medfast.password.service;

import com.vention.medfast.email.model.EmailTokenType;
import com.vention.medfast.email.model.EmailVerificationToken;
import com.vention.medfast.email.service.EmailVerificationTokenService;
import com.vention.medfast.password.dto.SetPasswordRequestDto;
import com.vention.medfast.user.model.Status;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PasswordManagementServiceImplTest {

	@Mock
	private UserService userService;

	@Mock
	private EmailVerificationTokenService tokenService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private PasswordManagementServiceImpl passwordManagementService;

	@Test
	void shouldSetPasswordAndActivateUserAndRemoveTokenWhenDataIsValid() {
		String rawPassword = "NewStrongPassword123!";
		String tokenString = "valid_token_string";
		SetPasswordRequestDto request = new SetPasswordRequestDto(tokenString, rawPassword, rawPassword);

		User user = new User();
		user.setId(1L);
		user.setStatus(Status.UNVERIFIED);

		EmailVerificationToken token = new EmailVerificationToken();
		token.setUser(user);

		given(tokenService.validateToken(tokenString, EmailTokenType.SET_PASSWORD)).willReturn(token);
		given(passwordEncoder.encode(rawPassword)).willReturn("encoded_hash");

		passwordManagementService.setPassword(request);

		assertThat(user.getPasswordHash()).isEqualTo("encoded_hash");
		assertThat(user.getStatus()).isEqualTo(Status.ACTIVE);

		verify(tokenService).deleteToken(token);
	}

	@Test
	void shouldSetPasswordButNotChangeStatusWhenUserIsAlreadyActive() {
		String rawPassword = "NewStrongPassword123!";
		String tokenString = "valid_token_string";
		SetPasswordRequestDto request = new SetPasswordRequestDto(tokenString, rawPassword, rawPassword);

		User user = new User();
		user.setStatus(Status.ACTIVE);

		EmailVerificationToken token = new EmailVerificationToken();
		token.setUser(user);

		given(tokenService.validateToken(tokenString, EmailTokenType.SET_PASSWORD)).willReturn(token);
		given(passwordEncoder.encode(rawPassword)).willReturn("encoded_hash");

		passwordManagementService.setPassword(request);

		assertThat(user.getPasswordHash()).isEqualTo("encoded_hash");
		assertThat(user.getStatus()).isEqualTo(Status.ACTIVE); // Should remain ACTIVE
		verify(tokenService).deleteToken(token);
	}

	@Test
	void shouldThrowExceptionWhenPasswordsDoNotMatch() {
		SetPasswordRequestDto request = new SetPasswordRequestDto("token", "passA", "passB");

		assertThatThrownBy(() -> passwordManagementService.setPassword(request))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("Passwords do not match");

		verify(tokenService, never()).validateToken(any(), any());
		verify(passwordEncoder, never()).encode(any());
	}

	@Test
	void shouldThrowExceptionWhenTokenIsInvalidOrExpired() {
		SetPasswordRequestDto request = new SetPasswordRequestDto("invalid_token", "pass", "pass");

		given(tokenService.validateToken("invalid_token", EmailTokenType.SET_PASSWORD))
			.willThrow(new EntityNotFoundException("Token not found"));

		assertThatThrownBy(() -> passwordManagementService.setPassword(request))
			.isInstanceOf(EntityNotFoundException.class)
			.hasMessage("Token not found");

		verify(passwordEncoder, never()).encode(any());
		verify(tokenService, never()).deleteToken(any());
	}

}
