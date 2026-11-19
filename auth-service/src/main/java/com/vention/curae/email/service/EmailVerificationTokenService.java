package com.vention.curae.email.service;

import com.vention.curae.email.model.EmailTokenType;
import com.vention.curae.email.model.EmailVerificationToken;
import com.vention.curae.user.model.User;

public interface EmailVerificationTokenService {

	String createForPatient(User user);

	String createDoctorWelcomeToken(User user);

	void verify(String rawToken);

	EmailVerificationToken validateToken(String rawToken, EmailTokenType expectedType);

	void deleteToken(EmailVerificationToken token);

	EmailVerificationToken getLatestResetToken(String email, EmailTokenType type);

	EmailVerificationToken createToken(User user, String code);

	EmailVerificationToken updateToken(EmailVerificationToken token);

}
