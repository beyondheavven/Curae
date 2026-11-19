package com.vention.curae.registration.listener;

import com.vention.curae.email.service.EmailVerificationTokenService;
import com.vention.curae.event.AuthEventPublisher;
import com.vention.curae.registration.event.DoctorRegisteredEvent;
import com.vention.curae.user.model.User;
import com.vention.curae.user.service.UserService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class DoctorRegisteredListener {

	private final UserService userService;

	private final EmailVerificationTokenService tokenService;

	private final AuthEventPublisher eventPublisher;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(DoctorRegisteredEvent event) {
		User user = userService.findById(event.userId());
		String rawToken = tokenService.createDoctorWelcomeToken(user);
		eventPublisher.doctorWelcomeRequested(user.getEmail(), event.tempPassword(), rawToken);

		var req = event.request();
		eventPublisher.doctorProfileRegistered(new com.vention.curae.event.DoctorRegistrationEvent(user.getId(),
				user.getEmail(), user.getFirstName(), user.getLastName(),
				user.getBirthDate() != null ? user.getBirthDate().toString() : null, req.phoneNumber(),
				req.licenseNumber(), req.specializationIds(), Instant.now()));
	}

}
