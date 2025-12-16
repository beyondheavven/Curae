package com.vention.medfast.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private static final String UTF_8 = "UTF-8";
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendPasswordResetCode(String email, int code) {
        try {
            Context context = new Context();
            context.setVariable("resetCode", String.format("%04d", code));
            String html = templateEngine.process(
                "mail/reset-password",
                context
            );
            sendHtmlEmail(
                email,
                "Password reset code",
                html
            );
        }
        catch (Exception e) {
            log.error("Failed to send password reset email to {}", email, e);
        }
    }

    @Override
    public void sendEmailVerification(String email, String rawToken) {
        try {
            Context context = new Context();
            String verifyLink =
                "http://localhost:8080/api/auth/verify?token=" + rawToken;
            context.setVariable("verifyLink", verifyLink);
            String html = templateEngine.process(
                "mail/verify-email",
                context
            );
            sendHtmlEmail(
                email,
                "Email verification",
                html
            );
        }
        catch (Exception e) {
            log.error("Failed to send verification email to {}", email, e);
        }
    }

    private void sendHtmlEmail(String to, String subject, String html)
        throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper =
            new MimeMessageHelper(message, true, UTF_8);
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        mailSender.send(message);
    }
}
