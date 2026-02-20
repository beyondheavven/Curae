package com.vention.medfast.email.service;

import com.vention.medfast.api.exception.PasswordResetException;
import com.vention.medfast.email.mapper.EmailVerificationTokenMapper;
import com.vention.medfast.email.model.EmailTokenStatus;
import com.vention.medfast.email.model.EmailTokenType;
import com.vention.medfast.email.model.EmailVerificationToken;
import com.vention.medfast.email.properties.EmailVerificationProperties;
import com.vention.medfast.email.repository.EmailVerificationTokenRepository;
import com.vention.medfast.user.model.RoleType;
import com.vention.medfast.user.model.Status;
import com.vention.medfast.user.model.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@AllArgsConstructor
public class EmailVerificationTokenServiceImpl implements EmailVerificationTokenService {

	private final static int TOKEN_BYTE_LENGTH = 32;

	private final static String TOKEN_HASH_ALGORITHM = "SHA-256";

	private final static int TOKEN_HASH_LENGTH = 64;

	private static final String HEX_BYTE_FORMAT = "%02x";

	private final EmailVerificationTokenRepository emailVerificationTokenRepository;

	private final EmailVerificationTokenMapper emailVerificationTokenMapper;

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
		EmailVerificationToken token = emailVerificationTokenMapper.createResetPasswordToken(user, code,
				UUID.randomUUID().toString(), Instant.now().plusMillis(emailVerificationProperties.getTtlMs()));
		return emailVerificationTokenRepository.save(token);
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
		String code = String.format("%04d", ThreadLocalRandom.current().nextInt(0, 10000));
		token.setCode(code);
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

		Instant expiresAt = Instant.now().plusMillis(emailVerificationProperties.getTtlMs());
		log.debug("Creating token for userId={}", user.getId());

		EmailVerificationToken token = emailVerificationTokenMapper.buildToken(user, tokenHash, expiresAt, type);

		log.debug("Token mapped: type={}, status={}, hash={}", token.getType(), token.getStatus(),
				token.getTokenHash());

		emailVerificationTokenRepository.save(token);
		log.debug("Token saved, id={}", token.getId());

		return rawToken;
	}

	private static String sha256Hex(String value) {
		try {
			MessageDigest md = MessageDigest.getInstance(TOKEN_HASH_ALGORITHM);
			byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));

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
