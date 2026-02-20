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
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Transactional
@RequiredArgsConstructor
@Service
public class PasswordResetServiceImpl implements PasswordResetService {

	private final UserService userService;

	private final EmailVerificationTokenService tokenService;

	private final ApplicationEventPublisher eventPublisher;

	@Override
	public void sendResetCode(String email) {
		User user = userService.findByUsername(email);
		String code = String.format("%04d", ThreadLocalRandom.current().nextInt(0, 10000));
		EmailVerificationToken token;
		try {
			EmailVerificationToken oldToken = tokenService.getLatestResetToken(email, EmailTokenType.RESET_PASSWORD);
			token = tokenService.updateToken(oldToken, Instant.now());
		}
		catch (EntityNotFoundException e) {
			token = tokenService.createToken(user, code);
		}
		eventPublisher
			.publishEvent(new PasswordResetCreatedEvent(user, EmailTokenType.RESET_PASSWORD, token.getCode(), null));
	}

	@Override
	public String verifyCode(String email, String code) {
		EmailVerificationToken token = tokenService.getLatestResetToken(email, EmailTokenType.RESET_PASSWORD);
		validateVerificationRules(token, code);
		token.setStatus(EmailTokenStatus.VERIFIED);
		return token.getTokenHash();
	}

	private void validateVerificationRules(EmailVerificationToken token, String code) {
		Instant now = Instant.now();
		if (token.getBlockedUntil() != null && token.getBlockedUntil().isAfter(now)) {
			throw new PasswordResetException("Too many attempts. Try again later");
		}
		if (token.getExpiresAt().isBefore(now)) {
			throw new PasswordResetException("Code is expired");
		}
		if (!token.getCode().equals(code)) {
			throw new PasswordResetException("Code is wrong");
		}
	}

}
