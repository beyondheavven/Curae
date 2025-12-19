package com.vention.medfast.passwordReset.service;

import com.vention.medfast.mail.model.EmailToken;
import com.vention.medfast.mail.model.EmailTokenStatus;
import com.vention.medfast.mail.repository.EmailTokenRepository;
import com.vention.medfast.mail.token.EmailTokenGenerator;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import java.time.Instant;

@RequiredArgsConstructor
@Service
@Transactional
@Validated
public class PasswordManagementServiceImpl implements PasswordManagementService {

	private final UserService userService;

	private final EmailTokenRepository emailTokenRepository;

	private final PasswordEncoder passwordEncoder;

	@Override
	public void changePassword(@NotNull Long userId, @NotBlank String currentPassword,
			@NotBlank @Size(min = 8) String newPassword, @NotBlank String repeatPassword) {
		User user = userService.findById(userId);

		if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
			throw new BadCredentialsException("Incorrect current password");
		}
		if (!newPassword.equals(repeatPassword)) {
			throw new IllegalStateException("Passwords do not match");
		}
		if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
			throw new IllegalStateException("New password must be different from the old one");
		}
		user.setPasswordHash(passwordEncoder.encode(newPassword));
	}

	@Override
	public void resetPassword(@NotBlank String resetToken, @NotBlank @Size(min = 8) String newPassword,
			@NotBlank String repeatPassword) {
		String tokenHash = EmailTokenGenerator.hash(resetToken);
		EmailToken token = emailTokenRepository.findByTokenHash(tokenHash)
			.orElseThrow(() -> new EntityNotFoundException("Reset token not found"));
		if (token.getStatus() != EmailTokenStatus.VERIFIED) {
			throw new IllegalStateException("Reset token is not verified");
		}
		if (token.getExpiresAt().isBefore(Instant.now())) {
			throw new IllegalStateException("Reset token expired");
		}
		if (!newPassword.equals(repeatPassword)) {
			throw new IllegalStateException("Passwords do not match");
		}
		User user = token.getUser();
		if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
			throw new IllegalStateException("New password must be different from the old one");
		}
		user.setPasswordHash(passwordEncoder.encode(newPassword));
	}

}
