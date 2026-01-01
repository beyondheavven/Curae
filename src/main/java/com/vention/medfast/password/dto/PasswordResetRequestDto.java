package com.vention.medfast.password.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetRequestDto {

	@NotBlank
	private String email;

}
