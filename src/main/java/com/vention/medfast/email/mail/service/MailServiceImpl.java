package com.vention.medfast.email.mail.service;

import com.vention.medfast.email.mail.properties.AppMailProperties;
import com.vention.medfast.properties.AppProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Service
@AllArgsConstructor
public class MailServiceImpl implements MailService {

	private static final String UTF_8 = "UTF-8";

	private final JavaMailSender mailSender;

	private final SpringTemplateEngine templateEngine;

	private final AppMailProperties mailProps;

	private final AppProperties appProperties;

	@Override
	public void sendEmailVerification(String toEmail, String token) {
		String link = buildLink(mailProps.getPaths().getVerifyEmail(), token);

		sendTemplateEmail(toEmail, mailProps.getSubjects().getVerifyEmail(), mailProps.getTemplates().getVerifyEmail(),
				Map.of("verifyLink", link));
	}

	@Override
	public void sendPasswordResetCode(String toEmail, int code) {
		sendTemplateEmail(toEmail, mailProps.getSubjects().getResetPassword(),
				mailProps.getTemplates().getResetPassword(), Map.of("resetCode", String.format("%04d", code)));
	}

	private void sendTemplateEmail(String toEmail, String subject, String template, Map<String, Object> vars) {
		try {
			Context context = new Context();
			context.setVariables(vars);

			String html = templateEngine.process(template, context);

			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8);

			helper.setFrom(mailProps.getFrom());
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
		return UriComponentsBuilder.fromUriString(appProperties.getBaseUrl())
			.path(path)
			.queryParam("token", token)
			.build()
			.toUriString();
	}

}
