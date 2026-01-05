package com.vention.medfast.password.service;

import com.vention.medfast.api.exception.PasswordResetException;
import com.vention.medfast.email.mapper.EmailVerificationTokenMapper;
import com.vention.medfast.email.model.EmailTokenStatus;
import com.vention.medfast.email.model.EmailTokenType;
import com.vention.medfast.email.model.EmailVerificationToken;
import com.vention.medfast.email.repository.EmailVerificationTokenRepository;
import com.vention.medfast.email.service.EmailVerificationTokenService;
import com.vention.medfast.password.event.PasswordResetCreatedEvent;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Transactional
@RequiredArgsConstructor
@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserService userService;
    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailVerificationTokenService tokenService;
    private final ApplicationEventPublisher eventPublisher;
    private final EmailVerificationTokenMapper emailVerificationTokenMapper;

    @Override
	public void sendResetCode(String email) {
        User user = findUserByEmail(email);
        Instant now = Instant.now();
        EmailVerificationToken token = tokenRepository
            .findTopByUserEmailAndTypeOrderByCreatedAtDesc(email, EmailTokenType.RESET_PASSWORD)
            .map(existing -> updateExistingToken(existing, now))
            .orElseGet(() -> emailVerificationTokenMapper.createResetPasswordToken(user, Integer.valueOf(RandomStringUtils.randomNumeric(4)), UUID.randomUUID().toString(), now.plus(1, ChronoUnit.MINUTES)));
        tokenRepository.save(token);
        eventPublisher.publishEvent(
            new PasswordResetCreatedEvent(
                user,
                EmailTokenType.RESET_PASSWORD,
                token.getCode(),
                null
            )
        );
	}

	@Override
	public String verifyCode(String email, String code) {
        User user = findUserByEmail(email);
        EmailVerificationToken token = getLatestResetToken(email);
        validateVerificationRules(token, code);
        String resetToken = tokenService.createForResetPassword(user);
        token.setStatus(EmailTokenStatus.VERIFIED);
        tokenRepository.save(token);
        return resetToken;
	}

    private User findUserByEmail(String email) {
        return userService.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private EmailVerificationToken updateExistingToken(EmailVerificationToken token, Instant now) {
        validateResendRules(token, now);
        token.setResendCount(token.getResendCount() + 1);
        if (token.getResendCount() >= 3) {
            token.setBlockedUntil(now.plus(30, ChronoUnit.MINUTES));
        }
        token.setCode(Integer.valueOf(RandomStringUtils.randomNumeric(4)));
        token.setTokenHash(UUID.randomUUID().toString());
        token.setExpiresAt(now.plus(1, ChronoUnit.MINUTES));
        token.setStatus(EmailTokenStatus.SENT);
        return token;
    }

    private void validateResendRules(EmailVerificationToken token, Instant now) {
        if (token.getBlockedUntil() != null && token.getBlockedUntil().isAfter(now)) {
            throw new PasswordResetException("Too many attempts. Try again later");
        }
        if (token.getExpiresAt() != null && token.getExpiresAt().isAfter(now)) {
            throw new PasswordResetException("Please wait before resending code");
        }
    }

    private EmailVerificationToken getLatestResetToken(String email) {
        return tokenRepository
            .findTopByUserEmailAndTypeOrderByCreatedAtDesc(email, EmailTokenType.RESET_PASSWORD)
            .orElseThrow(() -> new EntityNotFoundException("Reset code not found"));
    }

    private void validateVerificationRules(EmailVerificationToken token, String code) {
        Instant now = Instant.now();
        if (token.getBlockedUntil() != null && token.getBlockedUntil().isAfter(now)) {
            throw new PasswordResetException("Too many attempts. Try again later");
        }
        if (token.getExpiresAt().isBefore(now)) {
            throw new PasswordResetException("Code is expired");
        }
        if (!token.getCode().toString().equals(code)) {
            throw new PasswordResetException("Code is wrong");
        }
    }
}
