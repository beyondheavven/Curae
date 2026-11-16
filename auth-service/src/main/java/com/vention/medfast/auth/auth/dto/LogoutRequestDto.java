package com.vention.medfast.auth.auth.dto;

import jakarta.validation.constraints.NotNull;

public record LogoutRequestDto(@NotNull(message = "refresh token is required") String refreshToken) {
}
