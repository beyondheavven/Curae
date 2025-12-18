package com.vention.medfast.mail.service;

public interface MailService {
    void sendPasswordResetCode(String email, int code);
    void sendEmailVerification(String email, String rawToken);
}
