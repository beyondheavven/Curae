package com.vention.medfast.auth.registration.listener;

import com.vention.medfast.email.mail.service.MailService;
import com.vention.medfast.email.service.EmailVerificationTokenService;
import com.vention.medfast.registration.event.PatientRegisteredEvent;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PatientRegisteredListener {

	private final UserService userService;

	private final EmailVerificationTokenService emailVerificationTokenService;

	private final MailService mailService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(PatientRegisteredEvent event) {
		User user = userService.findById(event.userId());
		String rawToken = emailVerificationTokenService.createForPatient(user);
		mailService.sendEmailVerification(user.getEmail(), rawToken);
	}

}
