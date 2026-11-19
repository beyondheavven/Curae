package com.vention.curae.appointment.snapshot.listener;

import com.vention.curae.appointment.snapshot.model.ServiceSnapshot;
import com.vention.curae.appointment.snapshot.model.UserSnapshot;
import com.vention.curae.appointment.snapshot.repository.ServiceSnapshotRepository;
import com.vention.curae.appointment.snapshot.repository.UserSnapshotRepository;
import com.vention.curae.event.DoctorRegistrationEvent;
import com.vention.curae.event.PatientRegistrationEvent;
import com.vention.curae.event.ProvidedServiceDeletedEvent;
import com.vention.curae.event.ProvidedServiceUpsertedEvent;
import com.vention.curae.event.UserIdentityUpdatedEvent;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotKafkaListener {

	private final UserSnapshotRepository userSnapshotRepository;

	private final ServiceSnapshotRepository serviceSnapshotRepository;

	@Transactional
	@KafkaListener(topics = "user.identity.updated", groupId = "appointment-service-group")
	public void handleUserIdentityUpdated(UserIdentityUpdatedEvent event) {
		log.info("Received UserIdentityUpdatedEvent for userId={}", event.userId());
		UserSnapshot snapshot = userSnapshotRepository.findById(event.userId()).orElseGet(UserSnapshot::new);
		snapshot.setUserId(event.userId());
		if (event.email() != null) {
			snapshot.setEmail(event.email());
		}
		if (event.firstName() != null) {
			snapshot.setFirstName(event.firstName());
		}
		if (event.lastName() != null) {
			snapshot.setLastName(event.lastName());
		}
		if (event.birthDate() != null && !event.birthDate().isBlank()) {
			snapshot.setBirthDate(LocalDate.parse(event.birthDate()));
		}
		if (event.status() != null) {
			snapshot.setStatus(event.status());
		}
		userSnapshotRepository.save(snapshot);
	}

	@Transactional
	@KafkaListener(topics = "user.patient-profile.registered", groupId = "appointment-service-group")
	public void handlePatientRegistered(PatientRegistrationEvent event) {
		log.info("Received PatientRegistrationEvent for userId={}", event.userId());
		upsertUser(event.userId(), event.email(), event.firstName(), event.lastName(), event.birthDate(), "UNVERIFIED");
	}

	@Transactional
	@KafkaListener(topics = "provider.doctor.registered", groupId = "appointment-service-group")
	public void handleDoctorRegistered(DoctorRegistrationEvent event) {
		log.info("Received DoctorRegistrationEvent for userId={}", event.userId());
		upsertUser(event.userId(), event.email(), event.firstName(), event.lastName(), event.birthDate(), "UNVERIFIED");
	}

	@Transactional
	@KafkaListener(topics = "provider.service.upserted", groupId = "appointment-service-group")
	public void handleServiceUpserted(ProvidedServiceUpsertedEvent event) {
		log.info("Received ProvidedServiceUpsertedEvent for serviceId={}", event.serviceId());
		ServiceSnapshot snapshot = serviceSnapshotRepository.findById(event.serviceId()).orElseGet(ServiceSnapshot::new);
		snapshot.setServiceId(event.serviceId());
		snapshot.setName(event.name());
		snapshot.setDescription(event.description());
		snapshot.setPrice(event.price());
		snapshot.setDurationMinutes(event.durationMinutes());
		serviceSnapshotRepository.save(snapshot);
	}

	@Transactional
	@KafkaListener(topics = "provider.service.deleted", groupId = "appointment-service-group")
	public void handleServiceDeleted(ProvidedServiceDeletedEvent event) {
		log.info("Received ProvidedServiceDeletedEvent for serviceId={}", event.serviceId());
		serviceSnapshotRepository.deleteById(event.serviceId());
	}

	private void upsertUser(Long userId, String email, String firstName, String lastName, String birthDate,
			String defaultStatus) {
		UserSnapshot snapshot = userSnapshotRepository.findById(userId).orElseGet(UserSnapshot::new);
		snapshot.setUserId(userId);
		if (email != null) {
			snapshot.setEmail(email);
		}
		if (firstName != null) {
			snapshot.setFirstName(firstName);
		}
		if (lastName != null) {
			snapshot.setLastName(lastName);
		}
		if (birthDate != null && !birthDate.isBlank()) {
			snapshot.setBirthDate(LocalDate.parse(birthDate));
		}
		if (snapshot.getStatus() == null) {
			snapshot.setStatus(defaultStatus);
		}
		userSnapshotRepository.save(snapshot);
	}

}