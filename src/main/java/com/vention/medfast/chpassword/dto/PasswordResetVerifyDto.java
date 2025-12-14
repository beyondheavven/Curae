package com.vention.medfast.chpassword.dto;
import lombok.Data;

@Data
public class PasswordResetVerifyDto {
    private String email;
    private String code;
}
