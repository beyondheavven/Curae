package com.vention.medfast.provider.listener;

import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.model.DoctorDetails;
import com.vention.medfast.doctor.repository.DoctorRepository;
import com.vention.medfast.event.DoctorRegistrationEvent;
import com.vention.medfast.event.UserIdentityUpdatedEvent;
import com.vention.medfast.provider.model.UserSnapshot;
import com.vention.medfast.provider.repository.UserSnapshotRepository;
import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.specialization.repository.SpecializationRepository;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProviderKafkaListener {

	private final DoctorRepository doctorRepository;

	private final UserSnapshotRepository userSnapshotRepository;

	private final SpecializationRepository specializationRepository;

	@Transactional
	@KafkaListener(topics = "provider.doctor.registered", groupId = "provider-service-group")
	public void handleDoctorRegistered(DoctorRegistrationEvent event) {
		log.info("Received DoctorRegistrationEvent for userId={}", event.userId());

		UserSnapshot snapshot = userSnapshotRepository.findById(event.userId()).orElseGet(UserSnapshot::new);
		snapshot.setUserId(event.userId());
		snapshot.setEmail(event.email());
		snapshot.setFirstName(event.firstName());
		snapshot.setLastName(event.lastName());
		if (event.birthDate() != null && !event.birthDate().isBlank()) {
			snapshot.setBirthDate(LocalDate.parse(event.birthDate()));
		}
		snapshot.setStatus("UNVERIFIED");
		userSnapshotRepository.save(snapshot);

		if (doctorRepository.existsByUserId(event.userId())) {
			return;
		}

		Doctor doctor = new Doctor();
		doctor.setUserId(event.userId());
		doctor.setBio("");

		DoctorDetails details = new DoctorDetails();
		details.setPhoneNumber(event.phoneNumber());
		details.setLicenseNumber(event.licenseNumber());
		doctor.setDetails(details);

		if (event.specializationIds() != null && !event.specializationIds().isEmpty()) {
			Set<Specialization> specs = new HashSet<>(specializationRepository.findAllById(event.specializationIds()));
			doctor.setSpecializations(specs);
		}

		doctorRepository.save(doctor);
	}

	@Transactional
	@KafkaListener(topics = "user.identity.updated", groupId = "provider-service-group")
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

}
