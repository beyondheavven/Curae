package com.vention.medfast.email.service;

import com.vention.medfast.email.mapper.EmailVerificationTokenMapper;
import com.vention.medfast.email.model.EmailVerificationToken;
import com.vention.medfast.email.properties.EmailVerificationProperties;
import com.vention.medfast.email.repository.EmailVerificationTokenRepository;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.model.Status;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.user.model.RoleType;
import com.vention.medfast.user.model.User;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

	private final PatientService patientService;

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

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String createForUser(User user) {

		if (user.getId() == null) {
			throw new IllegalStateException("User must be persisted before creating token");
		}

		if (user.getRole() != RoleType.PATIENT) {
			throw new IllegalStateException("Email verification is allowed only for PATIENT");
		}

		byte[] randomBytes = new byte[TOKEN_BYTE_LENGTH];
		secureRandom.nextBytes(randomBytes);

		String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

		String tokenHash = sha256Hex(rawToken);

		Instant expiresAt = Instant.now().plusMillis(emailVerificationProperties.getTtlMs());
		log.info("Creating token for userId={}", user.getId());
		EmailVerificationToken token = emailVerificationTokenMapper.buildToken(user, tokenHash, expiresAt);
		log.info("Token mapped: type={}, status={}, hash={}", token.getType(), token.getStatus(), token.getTokenHash());
		emailVerificationTokenRepository.save(token);
		log.info("Token saved, id={}", token.getId());

		return rawToken;
	}

	@Transactional
	@Override
	public void verify(String rawToken) {
		String hash = sha256Hex(rawToken);

		EmailVerificationToken token = emailVerificationTokenRepository.findByTokenHash(hash)
			.orElseThrow(() -> new IllegalStateException("Token not found"));

		if (token.getExpiresAt().isBefore(Instant.now())) {
			throw new IllegalStateException("Token expired");
		}

		User user = token.getUser();

		if (user.getRole() != RoleType.PATIENT) {
			throw new IllegalStateException("Email verification is allowed only for PATIENT");
		}

		Patient patient = patientService.getPatientByUserId(user.getId());

		patient.setStatus(Status.ACTIVE);

		emailVerificationTokenRepository.delete(token);
	}

}
