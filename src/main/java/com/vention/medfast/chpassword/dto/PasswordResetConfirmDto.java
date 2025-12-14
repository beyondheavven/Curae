package com.vention.medfast.chpassword.dto;

import lombok.Data;

@Data
public class PasswordResetConfirmDto {
    private String email;
    private String newPassword;
    private String repeatPassword;
}
