package com.vention.medfast.email.service;

import com.vention.medfast.user.model.User;

public interface EmailVerificationTokenService {

	String createForUser(User user);

	void verify(String rawToken);

}
