package com.vention.medfast.api.exception;

import org.springframework.security.core.AuthenticationException;

public class EmailAlreadyExistsException extends AuthenticationException {

	public EmailAlreadyExistsException(String msg) {
		super(msg);
	}

}
