package com.vention.medfast.profile.listener;

import com.vention.medfast.email.mail.service.MailService;
import com.vention.medfast.email.service.EmailVerificationTokenService;
import com.vention.medfast.profile.event.EmailChangedEvent;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@AllArgsConstructor
public class EmailChangedEventListener {

	private final UserService userService;

	private final EmailVerificationTokenService emailVerificationTokenService;

	private final MailService mailService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(EmailChangedEvent event) {

		User user = userService.findById(event.userId());

		String rawToken = emailVerificationTokenService.createForPatient(user);

		mailService.sendEmailVerification(event.newEmail(), rawToken);

		log.info("Email verification sent after email change. userId={}, email={}", event.userId(), event.newEmail());
	}

}
