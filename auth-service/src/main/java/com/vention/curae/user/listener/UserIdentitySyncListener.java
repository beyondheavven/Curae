package com.vention.curae.user.listener;

import com.vention.curae.email.service.EmailVerificationTokenService;
import com.vention.curae.event.AuthEventPublisher;
import com.vention.curae.event.UserIdentityUpdatedEvent;
import com.vention.curae.event.UserProfileIdentitySyncRequestedEvent;
import com.vention.curae.user.model.Status;
import com.vention.curae.user.model.User;
import com.vention.curae.user.repository.UserRepository;
import java.time.Instant;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserIdentitySyncListener {

	private final UserRepository userRepository;

	private final EmailVerificationTokenService emailVerificationTokenService;

	private final AuthEventPublisher authEventPublisher;

	@Transactional
	@KafkaListener(topics = "user.profile.identity-sync-requested", groupId = "auth-service-group")
	public void handleIdentitySyncRequested(UserProfileIdentitySyncRequestedEvent event) {
		log.info("Received UserProfileIdentitySyncRequestedEvent for userId={}, emailChanged={}", event.userId(),
				event.emailChanged());

		User user = userRepository.findById(event.userId()).orElse(null);
		if (user == null) {
			log.warn("User not found for identity sync, userId={}", event.userId());
			return;
		}

		boolean changed = false;
		if (event.firstName() != null && !event.firstName().equals(user.getFirstName())) {
			user.setFirstName(event.firstName());
			changed = true;
		}
		if (event.lastName() != null && !event.lastName().equals(user.getLastName())) {
			user.setLastName(event.lastName());
			changed = true;
		}
		if (event.birthDate() != null && !event.birthDate().isBlank()) {
			LocalDate birthDate = LocalDate.parse(event.birthDate());
			if (!birthDate.equals(user.getBirthDate())) {
				user.setBirthDate(birthDate);
				changed = true;
			}
		}
		if (event.profileUrl() != null && !event.profileUrl().equals(user.getProfileUrl())) {
			user.setProfileUrl(event.profileUrl());
			changed = true;
		}

		boolean emailApplied = false;
		if (event.emailChanged() && event.email() != null && !event.email().isBlank()
				&& !event.email().equalsIgnoreCase(user.getEmail())) {
			if (userRepository.existsByEmail(event.email())) {
				log.warn("Skipping identity sync email change due to conflict, userId={}, email={}", event.userId(),
						event.email());
			}
			else {
				user.setEmail(event.email());
				user.setStatus(Status.UNVERIFIED);
				changed = true;
				emailApplied = true;
			}
		}

		if (changed) {
			userRepository.save(user);
		}

		if (emailApplied) {
			String rawToken = emailVerificationTokenService.createForPatient(user);
			authEventPublisher.userRegistered(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(),
					rawToken);
		}

		if (changed || event.emailChanged()) {
			authEventPublisher.userIdentityUpdated(new UserIdentityUpdatedEvent(user.getId(), user.getEmail(),
					user.getFirstName(), user.getLastName(),
					user.getBirthDate() != null ? user.getBirthDate().toString() : null, user.getStatus().name(),
					Instant.now()));
		}
	}

}