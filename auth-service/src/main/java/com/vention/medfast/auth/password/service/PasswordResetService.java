package com.vention.medfast.auth.password.service;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public interface PasswordResetService {

	void sendResetCode(@NotBlank @Email String email);

	String verifyCode(@NotBlank @Email String email, @NotBlank String code);

}
