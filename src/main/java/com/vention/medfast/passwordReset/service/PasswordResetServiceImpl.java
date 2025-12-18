package com.vention.medfast.passwordReset.service;

import com.vention.medfast.mail.model.EmailToken;
import com.vention.medfast.mail.model.EmailTokenStatus;
import com.vention.medfast.mail.model.EmailTokenType;
import com.vention.medfast.mail.repository.EmailTokenRepository;
import com.vention.medfast.mail.token.EmailTokenCreatedEvent;
import com.vention.medfast.mail.token.EmailTokenGenerator;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.repository.UserRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.data.domain.Sort;

@Transactional
@RequiredArgsConstructor
@Service
@Validated
public class PasswordResetServiceImpl implements PasswordResetService {

	private final UserRepository userRepository;

	private final EmailTokenRepository emailTokenRepository;

	private final ApplicationEventPublisher eventPublisher;

	@Override
	public void sendResetCode(@NotBlank @Email String email) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
		int code = EmailTokenGenerator.generateCode();
		EmailToken token = new EmailToken();
		token.setUser(user);
		token.setType(EmailTokenType.RESET_PASSWORD);
		token.setStatus(EmailTokenStatus.SENT);
		token.setCode(code);
		token.setExpiresAt(Instant.now().plus(4, ChronoUnit.MINUTES));
		emailTokenRepository.save(token);
		eventPublisher.publishEvent(new EmailTokenCreatedEvent(user, EmailTokenType.RESET_PASSWORD, code, null));
	}

	@Override
	public String verifyCode(@NotBlank @Email String email, @NotBlank String code) {
		EmailToken probe = new EmailToken();
		probe.setType(EmailTokenType.RESET_PASSWORD);
		probe.setStatus(EmailTokenStatus.SENT);
		User user = new User();
		user.setEmail(email);
		probe.setUser(user);
		Example<EmailToken> example = Example.of(probe);
		EmailToken token = emailTokenRepository
			.findAll(example, PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt")))
			.stream()
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Reset token not found"));
		if (token.getExpiresAt().isBefore(Instant.now())) {
			throw new IllegalArgumentException("Reset code expired");
		}
		if (!token.getCode().toString().equals(code)) {
			throw new IllegalArgumentException("Invalid reset code");
		}
		String rawResetToken = EmailTokenGenerator.generateRawToken();
		token.setTokenHash(EmailTokenGenerator.hash(rawResetToken));
		token.setStatus(EmailTokenStatus.VERIFIED);
		emailTokenRepository.save(token);
		return rawResetToken;
	}

}
