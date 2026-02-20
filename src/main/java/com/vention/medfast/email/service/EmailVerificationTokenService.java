package com.vention.medfast.email.service;

import com.vention.medfast.email.model.EmailTokenType;
import com.vention.medfast.email.model.EmailVerificationToken;
import com.vention.medfast.user.model.User;

import java.time.Instant;
import java.util.Optional;

public interface EmailVerificationTokenService {

	String createForPatient(User user);

	String createDoctorWelcomeToken(User user);

	void verify(String rawToken);

	EmailVerificationToken validateToken(String rawToken, EmailTokenType expectedType);

	void deleteToken(EmailVerificationToken token);

	EmailVerificationToken getLatestResetToken(String email, EmailTokenType type);

	EmailVerificationToken createToken(User user, String code);

	EmailVerificationToken updateToken(EmailVerificationToken token, Instant now);

}
