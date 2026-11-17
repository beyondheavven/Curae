package com.vention.medfast.notification.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSenderService {

	private static final String UTF_8 = "UTF-8";

	private final JavaMailSender mailSender;

	private final SpringTemplateEngine templateEngine;

	@Value("${app.mail.from:${spring.mail.username:}}")
	private String from;

	@Value("${app.base-url:http://localhost:8080}")
	private String baseUrl;

	@Value("${app.mail.paths.verify-email:/auth/verify}")
	private String verifyEmailPath;

	@Value("${app.mail.paths.doctor-welcome:/account/set-password}")
	private String doctorWelcomePath;

	public void sendEmailVerification(String toEmail, String token) {
		send(toEmail, "Email verification", "mail/verify-email",
				Map.of("verifyLink", buildLink(verifyEmailPath, token)));
	}

	public void sendPasswordResetCode(String toEmail, String code) {
		send(toEmail, "Password reset code", "mail/reset-password", Map.of("resetCode", code));
	}

	public void sendDoctorWelcome(String toEmail, String temporaryPassword, String token) {
		send(toEmail, "Welcome to Medfast", "mail/doctor-welcome",
				Map.of("tempPassword", temporaryPassword, "setupLink", buildLink(doctorWelcomePath, token)));
	}

	private void send(String toEmail, String subject, String template, Map<String, Object> variables) {
		try {
			Context context = new Context();
			context.setVariables(variables);
			String html = templateEngine.process(template, context);

			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8);
			helper.setFrom(from);
			helper.setTo(toEmail);
			helper.setSubject(subject);
			helper.setText(html, true);
			mailSender.send(message);
			log.info("Email sent to {}", toEmail);
		}
		catch (MessagingException e) {
			log.error("Failed to send email to {}", toEmail, e);
			throw new IllegalStateException("Failed to send email", e);
		}
	}

	private String buildLink(String path, String token) {
		return UriComponentsBuilder.fromUriString(baseUrl).path(path).queryParam("token", token).build().toUriString();
	}

}
