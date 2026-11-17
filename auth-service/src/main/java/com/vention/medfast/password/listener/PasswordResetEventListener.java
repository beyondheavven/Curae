package com.vention.medfast.password.listener;

import com.vention.medfast.event.AuthEventPublisher;
import com.vention.medfast.password.event.PasswordResetCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordResetEventListener {

	private final AuthEventPublisher eventPublisher;

	@EventListener
	public void handleEmailTokenCreated(PasswordResetCreatedEvent event) {
		if (event.code() == null) {
			log.error("RESET_PASSWORD event without code. userId={}", event.user().getId());
			return;
		}
		eventPublisher.passwordResetCodeRequested(event.user().getEmail(), event.code());
	}

}
