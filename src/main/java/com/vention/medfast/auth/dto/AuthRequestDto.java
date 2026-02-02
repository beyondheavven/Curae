package com.vention.medfast.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthRequestDto(@Schema(example = "Test@test.com") String email,

		@Schema(example = "pass") String password) {
}
