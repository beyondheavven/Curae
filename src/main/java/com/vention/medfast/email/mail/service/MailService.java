package com.vention.medfast.email.mail.service;

public interface MailService {

	void sendEmailVerification(String toEmail, String verificationToken);

	void sendPasswordResetCode(String toEmail, int code);

	void sendDoctorWelcomeEmail(String toEmail, String tempPassword, String token);

}
