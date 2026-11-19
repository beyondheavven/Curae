package com.vention.curae.password.service;

import com.vention.curae.email.model.EmailTokenStatus;
import com.vention.curae.email.model.EmailTokenType;
import com.vention.curae.email.model.EmailVerificationToken;
import com.vention.curae.email.service.EmailVerificationTokenService;
import com.vention.curae.password.dto.SetPasswordRequestDto;
import com.vention.curae.user.model.Status;
import com.vention.curae.user.model.User;
import com.vention.curae.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@RequiredArgsConstructor
@Service
@Transactional
@Validated
public class PasswordManagementServiceImpl implements PasswordManagementService {

	private final UserService userService;

	private final EmailVerificationTokenService emailVerificationTokenService;

	private final PasswordEncoder passwordEncoder;

	@Override
	public void changePassword(Long userId, String currentPassword, String newPassword, String repeatPassword) {
		User user = userService.findById(userId);

		if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
			throw new BadCredentialsException("Incorrect current password");
		}

		validateNewPassword(user, newPassword, repeatPassword);

		user.setPasswordHash(passwordEncoder.encode(newPassword));
	}

	@Override
	public void resetPassword(String resetToken, String newPassword, String repeatPassword) {
		EmailVerificationToken token = emailVerificationTokenService.validateToken(resetToken,
				EmailTokenType.RESET_PASSWORD);

		if (token.getStatus() != EmailTokenStatus.VERIFIED) {
			throw new IllegalStateException("Reset token is not verified");
		}

		User user = token.getUser();

		validateNewPassword(user, newPassword, repeatPassword);

		user.setPasswordHash(passwordEncoder.encode(newPassword));

		emailVerificationTokenService.deleteToken(token);
	}

	@Override
	public void setPassword(SetPasswordRequestDto requestDto) {
		if (!requestDto.newPassword().equals(requestDto.repeatPassword())) {
			throw new IllegalStateException("Passwords do not match");
		}

		EmailVerificationToken token = emailVerificationTokenService.validateToken(requestDto.token(),
				EmailTokenType.SET_PASSWORD);

		User user = token.getUser();
		user.setPasswordHash(passwordEncoder.encode(requestDto.newPassword()));

		if (user.getStatus() == Status.UNVERIFIED) {
			user.setStatus(Status.ACTIVE);
		}

		emailVerificationTokenService.deleteToken(token);
	}

	private void validateNewPassword(User user, String newPassword, String repeatPassword) {
		if (!newPassword.equals(repeatPassword)) {
			throw new IllegalStateException("Passwords do not match");
		}
		if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
			throw new BadCredentialsException("New password must be different from the old password");
		}
	}

}
