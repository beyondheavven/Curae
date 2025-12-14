package com.vention.medfast.dto;

import lombok.Data;

@Data
public class PasswordResetConfirmDto {
    private String email;
    private String newPassword;
    private String repeatPassword;
}
