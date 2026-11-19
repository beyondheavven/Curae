package com.vention.curae.auth.dto;

import jakarta.validation.constraints.NotNull;

public record LogoutRequestDto(@NotNull(message = "refresh token is required") String refreshToken) {
}
