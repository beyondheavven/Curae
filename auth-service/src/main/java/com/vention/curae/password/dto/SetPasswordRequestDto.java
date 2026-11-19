package com.vention.curae.password.dto;

import com.vention.curae.api.validation.annotation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SetPasswordRequestDto(@NotBlank(message = "Email verification token is required") String token,
		@NotBlank(message = "Password is required") @Size(min = 8,
				message = "Password must be at least 8 characters long") @ValidPassword String newPassword,
		@NotBlank(message = "Repeat password is required") String repeatPassword) {
}
