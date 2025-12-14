package com.vention.medfast.chpassword.service;

public interface EmailService {
    void sendPasswordResetCode(String email, String code);
}
