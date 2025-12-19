package com.vention.medfast.passwordReset.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetVerifyDto {

	@NotBlank
	private String email;

	@NotBlank
	private String code;

}
