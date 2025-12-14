package com.vention.medfast.service;

public interface EmailService {
    void sendPasswordResetCode(String email, String code);
}
