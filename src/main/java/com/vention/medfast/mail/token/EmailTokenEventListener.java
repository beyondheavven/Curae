package com.vention.medfast.mail.token;

import com.vention.medfast.email.mail.service.MailService;
import com.vention.medfast.mail.model.EmailTokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailTokenEventListener {

	private final MailService mailService;

	@EventListener
	public void handleEmailTokenCreated(EmailTokenCreatedEvent event) {
		if (event.type() == EmailTokenType.RESET_PASSWORD) {
			handlePasswordReset(event);
			return;
		}
		if (event.type() == EmailTokenType.VERIFY_EMAIL) {
			handleEmailVerification(event);
		}
	}

	private void handlePasswordReset(EmailTokenCreatedEvent event) {
		if (event.code() == null) {
			log.error("RESET_PASSWORD event without code. userId={}", event.user().getId());
			return;
		}
		mailService.sendPasswordResetCode(event.user().getEmail(), event.code());
	}

	private void handleEmailVerification(EmailTokenCreatedEvent event) {
		if (event.rawToken() == null) {
			log.error("VERIFY_EMAIL event without rawToken. userId={}", event.user().getId());
			return;
		}
		mailService.sendEmailVerification(event.user().getEmail(), event.rawToken());
	}

}
