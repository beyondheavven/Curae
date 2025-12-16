package com.vention.medfast.passwordReset.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetRequestDto {
    @NotBlank
    private String email;
}
