package com.vention.medfast.password.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetVerifyDto {

	@NotBlank
	@Email
	private String email;

	@NotBlank
	private String code;

}
