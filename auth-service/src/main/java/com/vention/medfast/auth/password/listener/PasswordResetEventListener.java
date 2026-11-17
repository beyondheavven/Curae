package com.vention.medfast.auth.password.listener;

import com.vention.medfast.email.mail.service.MailService;
import com.vention.medfast.password.event.PasswordResetCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordResetEventListener {

	private final MailService mailService;

	@EventListener
	public void handleEmailTokenCreated(PasswordResetCreatedEvent event) {
		if (event.code() == null) {
			log.error("RESET_PASSWORD event without code. userId={}", event.user().getId());
			return;
		}
		mailService.sendPasswordResetCode(event.user().getEmail(), event.code());
	}

}
