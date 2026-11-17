package com.vention.medfast.auth.dto;

import jakarta.validation.constraints.NotNull;

public record RefreshTokenRequestDto(@NotNull(message = "refresh token is required") String refreshToken) {
}
