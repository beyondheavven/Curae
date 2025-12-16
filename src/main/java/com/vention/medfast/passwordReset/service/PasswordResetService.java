package com.vention.medfast.passwordReset.service;

public interface PasswordResetService {
    void sendResetCode(String email);
    String verifyCode(String email, String code);
}
