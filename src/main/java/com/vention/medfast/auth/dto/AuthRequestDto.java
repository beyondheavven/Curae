package com.vention.medfast.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthRequestDto {

	@Schema(example = "Test@test.com")
	private String email;

	@Schema(example = "pass")
	private String password;

}
