package com.vention.curae.email.service;

import com.vention.curae.api.exception.PasswordResetException;
import com.vention.curae.email.mapper.EmailVerificationTokenMapper;
import com.vention.curae.email.model.EmailTokenStatus;
import com.vention.curae.email.model.EmailTokenType;
import com.vention.curae.email.model.EmailVerificationToken;
import com.vention.curae.email.properties.EmailVerificationProperties;
import com.vention.curae.email.repository.EmailVerificationTokenRepository;
import com.vention.curae.event.AuthEventPublisher;
import com.vention.curae.event.UserIdentityUpdatedEvent;
import com.vention.curae.user.model.RoleType;
import com.vention.curae.user.model.Status;
import com.vention.curae.user.model.User;
import jakarta.persistence.EntityNotFoundException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class EmailVerificationTokenServiceImpl implements EmailVerificationTokenService {

	private static final int TOKEN_BYTE_LENGTH = 32;

	private static final String TOKEN_HASH_ALGORITHM = "SHA-256";

	private static final int TOKEN_HASH_LENGTH = 64;

	private static final String HEX_BYTE_FORMAT = "%02x";

	private final EmailVerificationTokenRepository emailVerificationTokenRepository;

	private final EmailVerificationTokenMapper emailVerificationTokenMapper;

	private final AuthEventPublisher authEventPublisher;

	private final SecureRandom secureRandom = new SecureRandom();

	private final EmailVerificationProperties emailVerificationProperties;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String createForPatient(User user) {
		if (user.getRole() != RoleType.PATIENT) {
			throw new IllegalStateException("Email verification is allowed only for PATIENT");
		}
		return generateAndSaveToken(user, EmailTokenType.VERIFY_EMAIL);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String createDoctorWelcomeToken(User user) {
		if (user.getRole() != RoleType.DOCTOR) {
			throw new IllegalStateException("Doctor welcome token is allowed only for DOCTOR");
		}
		return generateAndSaveToken(user, EmailTokenType.SET_PASSWORD);
	}

	@Override
	@Transactional
	public void verify(String rawToken) {
		EmailVerificationToken token = validateToken(rawToken, EmailTokenType.VERIFY_EMAIL);
		User user = token.getUser();

		if (user.getRole() != RoleType.PATIENT) {
			throw new IllegalStateException("Email verification is allowed only for PATIENT");
		}

		user.setStatus(Status.ACTIVE);
		emailVerificationTokenRepository.delete(token);
		authEventPublisher.userIdentityUpdated(new UserIdentityUpdatedEvent(user.getId(), user.getEmail(),
				user.getFirstName(), user.getLastName(),
				user.getBirthDate() != null ? user.getBirthDate().toString() : null, user.getStatus().name(),
				Instant.now()));
	}

	@Override
	@Transactional
	public EmailVerificationToken validateToken(String tokenHash, EmailTokenType expectedType) {
		EmailVerificationToken token = emailVerificationTokenRepository.findByTokenHash(tokenHash)
			.orElseThrow(() -> new EntityNotFoundException("Token not found"));

		if (token.getExpiresAt().isBefore(Instant.now())) {
			throw new IllegalStateException("Token expired");
		}
		if (token.getType() != expectedType) {
			throw new IllegalStateException("Invalid token type");
		}
		return token;
	}

	@Override
	@Transactional
	public void deleteToken(EmailVerificationToken token) {
		emailVerificationTokenRepository.delete(token);
	}

	@Override
	public EmailVerificationToken getLatestResetToken(String email, EmailTokenType type) {
		return emailVerificationTokenRepository.findTopByUserEmailAndTypeOrderByCreatedAtDesc(email, type)
			.orElseThrow(() -> new EntityNotFoundException("Token not found"));
	}

	@Override
	@Transactional
	public EmailVerificationToken createToken(User user, String code) {
		return emailVerificationTokenRepository.save(emailVerificationTokenMapper.createResetPasswordToken(user, code,
				UUID.randomUUID().toString(), Instant.now().plusMillis(emailVerificationProperties.getTtlMs())));
	}

	@Override
	@Transactional
	public EmailVerificationToken updateToken(EmailVerificationToken token) {
		Instant now = Instant.now();
		validateResendRules(token, now);
		token.setResendCount(token.getResendCount() + 1);
		if (token.getResendCount() >= 3) {
			token.setBlockedUntil(now.plus(30, ChronoUnit.MINUTES));
		}
		token.setCode(String.format("%04d", ThreadLocalRandom.current().nextInt(0, 10000)));
		token.setTokenHash(UUID.randomUUID().toString());
		token.setExpiresAt(now.plusMillis(emailVerificationProperties.getTtlMs()));
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

	private String generateAndSaveToken(User user, EmailTokenType type) {
		if (user.getId() == null) {
			throw new IllegalStateException("User must be persisted before creating token");
		}

		byte[] randomBytes = new byte[TOKEN_BYTE_LENGTH];
		secureRandom.nextBytes(randomBytes);
		String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
		String tokenHash = sha256Hex(rawToken);

		log.debug("Creating token for userId={}, type={}", user.getId(), type);
		EmailVerificationToken token = emailVerificationTokenMapper.buildToken(user, tokenHash,
				Instant.now().plusMillis(emailVerificationProperties.getTtlMs()), type);
		emailVerificationTokenRepository.save(token);
		return rawToken;
	}

	private static String sha256Hex(String value) {
		try {
			byte[] digest = MessageDigest.getInstance(TOKEN_HASH_ALGORITHM)
				.digest(value.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder(TOKEN_HASH_LENGTH);
			for (byte b : digest) {
				sb.append(String.format(HEX_BYTE_FORMAT, b));
			}
			return sb.toString();
		}
		catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA-256 not available", e);
		}
	}

}
