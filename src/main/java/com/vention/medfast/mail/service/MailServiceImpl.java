package com.vention.medfast.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.mail.autoconfigure.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.springframework.core.env.Environment;


@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private static final String UTF_8 = "UTF-8";

    // templates
    private static final String RESET_PASSWORD_TEMPLATE = "mail/reset-password";
    private static final String VERIFY_EMAIL_TEMPLATE = "mail/verify-email";

    // subjects
    private static final String RESET_PASSWORD_SUBJECT = "Password reset code";
    private static final String VERIFY_EMAIL_SUBJECT = "Email verification";

    // property keys
    private static final String VERIFY_EMAIL_URL_PROPERTY =
        "app.email-verification.verify-url";

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final MailProperties mailProperties;
    private final Environment environment;

    @Override
    public void sendPasswordResetCode(String email, int code) {
        try {
            Context context = new Context();
            context.setVariable("resetCode", String.format("%04d", code));
            String html = templateEngine.process(
                RESET_PASSWORD_TEMPLATE,
                context
            );
            sendHtmlEmail(
                email,
                RESET_PASSWORD_SUBJECT,
                html
            );
        }
        catch (Exception e) {
            log.error(
                "Failed to send password reset email to {}",
                email,
                e
            );
        }
    }

    @Override
    public void sendEmailVerification(String email, String rawToken) {
        try {
            String verifyUrl = environment.getProperty(
                VERIFY_EMAIL_URL_PROPERTY
            );
            if (verifyUrl == null) {
                throw new IllegalStateException(
                    "Property '" + VERIFY_EMAIL_URL_PROPERTY + "' is not configured"
                );
            }
            Context context = new Context();
            context.setVariable(
                "verifyLink",
                verifyUrl + rawToken
            );
            String html = templateEngine.process(
                VERIFY_EMAIL_TEMPLATE,
                context
            );
            sendHtmlEmail(
                email,
                VERIFY_EMAIL_SUBJECT,
                html
            );
        }
        catch (Exception e) {
            log.error(
                "Failed to send verification email to {}",
                email,
                e
            );
        }
    }

    private void sendHtmlEmail(String to, String subject, String html)
        throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper =
            new MimeMessageHelper(message, true, UTF_8);
        helper.setFrom(mailProperties.getUsername());
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        mailSender.send(message);
    }
}
