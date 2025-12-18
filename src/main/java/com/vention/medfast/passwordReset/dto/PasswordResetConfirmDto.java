package com.vention.medfast.passwordReset.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordResetConfirmDto {
    @NotBlank
    private String resetToken;
    @NotBlank
    @Size(min = 8)
    private String newPassword;
    @NotBlank
    private String repeatPassword;
}
