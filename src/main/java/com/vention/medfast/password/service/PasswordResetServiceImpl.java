package com.vention.medfast.password.service;

import com.vention.medfast.email.model.EmailTokenStatus;
import com.vention.medfast.email.model.EmailTokenType;
import com.vention.medfast.email.model.EmailVerificationToken;
import com.vention.medfast.email.repository.EmailVerificationTokenRepository;
import com.vention.medfast.email.service.EmailVerificationTokenService;
import com.vention.medfast.password.event.PasswordResetCreatedEvent;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Transactional
@RequiredArgsConstructor
@Service
@Validated
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserService userService;
    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailVerificationTokenService tokenService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
	public void sendResetCode(@NotBlank @Email String email) {
        User user = userService.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Instant now = Instant.now();
        Optional<EmailVerificationToken> tokenOpt =
            tokenRepository.findTopByUserEmailAndTypeOrderByCreatedAtDesc(
                email, EmailTokenType.RESET_PASSWORD
            );
        EmailVerificationToken token;
        if (tokenOpt.isPresent()) {
            token = tokenOpt.get();
            if (token.getBlockedUntil() != null && token.getBlockedUntil().isAfter(now)) {
                throw new IllegalStateException("Too many attempts. Try again later");
            }
            if (token.getExpiresAt() != null && token.getExpiresAt().isAfter(now)) {
                throw new IllegalStateException("Please wait before resending code");
            }
            token.setResendCount(token.getResendCount() + 1);
            if (token.getResendCount() >= 3) {
                token.setBlockedUntil(now.plus(30, ChronoUnit.MINUTES));
            }

        } else {
            token = new EmailVerificationToken();
            token.setUser(user);
            token.setType(EmailTokenType.RESET_PASSWORD);
            token.setResendCount(0);
        }
        token.setTokenHash(UUID.randomUUID().toString());
        int code = ThreadLocalRandom.current().nextInt(1000, 10000);
        token.setCode(code);
        token.setExpiresAt(now.plus(1, ChronoUnit.MINUTES));
        token.setStatus(EmailTokenStatus.SENT);
        tokenRepository.save(token);
        eventPublisher.publishEvent(
            new PasswordResetCreatedEvent(
                user,
                EmailTokenType.RESET_PASSWORD,
                code,
                null
            )
        );
	}

	@Override
	public String verifyCode(@NotBlank @Email String email, @NotBlank String code) {
        User user = userService.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        EmailVerificationToken token = tokenRepository
            .findTopByUserEmailAndTypeOrderByCreatedAtDesc(
                email, EmailTokenType.RESET_PASSWORD
            )
            .orElseThrow(() -> new EntityNotFoundException("Reset code not found"));
        Instant now = Instant.now();
        if (token.getBlockedUntil() != null && token.getBlockedUntil().isAfter(now)) {
            throw new IllegalStateException("Too many attempts. Try again later");
        }
        if (token.getExpiresAt().isBefore(now)) {
            throw new IllegalStateException("Code expired");
        }
        if (token.getCode() == null || !token.getCode().toString().equals(code)) {
            throw new IllegalArgumentException("Code is wrong");
        }
        String rawResetToken = tokenService
            .createForResetPassword(user);
        token.setStatus(EmailTokenStatus.VERIFIED);
        tokenRepository.save(token);
        return rawResetToken;
	}

}
