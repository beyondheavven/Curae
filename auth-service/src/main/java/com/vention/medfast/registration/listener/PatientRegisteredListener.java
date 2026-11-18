package com.vention.medfast.registration.listener;

import com.vention.medfast.email.service.EmailVerificationTokenService;
import com.vention.medfast.event.AuthEventPublisher;
import com.vention.medfast.registration.event.PatientRegisteredEvent;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.service.UserService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PatientRegisteredListener {

	private final UserService userService;

	private final EmailVerificationTokenService emailVerificationTokenService;

	private final AuthEventPublisher eventPublisher;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(PatientRegisteredEvent event) {
		User user = userService.findById(event.userId());
		String rawToken = emailVerificationTokenService.createForPatient(user);
		eventPublisher.userRegistered(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), rawToken);

		var person = event.person();
		var addr = event.address();
		eventPublisher.patientProfileRegistered(new com.vention.medfast.event.PatientRegistrationEvent(user.getId(),
				user.getEmail(), user.getFirstName(), user.getLastName(),
				user.getBirthDate() != null ? user.getBirthDate().toString() : null,
				person.getGender() != null ? person.getGender().name() : null, person.getCitizenship(),
				new com.vention.medfast.event.PatientRegistrationEvent.AddressData(addr.getStreet(), addr.getCity(),
						addr.getApartment(), addr.getHouseNumber(), addr.getState(), addr.getZipCode(),
						addr.getPhoneNumber()),
				Instant.now()));
	}

}
