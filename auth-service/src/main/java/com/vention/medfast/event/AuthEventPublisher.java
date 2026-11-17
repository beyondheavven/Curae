package com.vention.medfast.event;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthEventPublisher {

	private final KafkaTemplate<Object, Object> kafkaTemplate;

	public void userRegistered(Long userId, String email, String firstName, String lastName, String verificationToken) {
		kafkaTemplate.send("user.registered",
				new UserRegisteredEvent(userId, email, firstName, lastName, verificationToken, Instant.now()));
	}

	public void doctorWelcomeRequested(String email, String temporaryPassword, String token) {
		kafkaTemplate.send("email.doctor-welcome-requested",
				new DoctorWelcomeRequestedEvent(email, temporaryPassword, token, Instant.now()));
	}

	public void passwordResetCodeRequested(String email, String code) {
		kafkaTemplate.send("email.password-reset-requested",
				new PasswordResetCodeRequestedEvent(email, code, Instant.now()));
	}

}
