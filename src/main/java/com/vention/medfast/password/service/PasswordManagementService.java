package com.vention.medfast.password.service;

import com.vention.medfast.password.dto.SetPasswordRequestDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public interface PasswordManagementService {

	void changePassword(@NotNull Long userId, @NotBlank String currentPassword,
			@NotBlank @Size(min = 8) String newPassword, @NotBlank String repeatPassword);

	void resetPassword(@NotBlank String resetToken, @NotBlank @Size(min = 8) String newPassword,
			@NotBlank String repeatPassword);

	void setPassword(SetPasswordRequestDto requestDto);

}
