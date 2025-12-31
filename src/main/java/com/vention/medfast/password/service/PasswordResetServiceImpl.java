package com.vention.medfast.password.service;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Transactional
@RequiredArgsConstructor
@Service
@Validated
public class PasswordResetServiceImpl implements PasswordResetService {

	@Override
	public void sendResetCode(@NotBlank @Email String email) {

	}

	@Override
	public String verifyCode(@NotBlank @Email String email, @NotBlank String code) {

		return null;
	}

}
