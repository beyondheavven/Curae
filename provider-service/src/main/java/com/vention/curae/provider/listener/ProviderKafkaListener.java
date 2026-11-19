package com.vention.curae.provider.listener;

import com.vention.curae.doctor.model.Doctor;
import com.vention.curae.doctor.model.DoctorDetails;
import com.vention.curae.doctor.repository.DoctorRepository;
import com.vention.curae.event.DoctorRegistrationEvent;
import com.vention.curae.event.UserIdentityUpdatedEvent;
import com.vention.curae.provider.model.UserSnapshot;
import com.vention.curae.provider.repository.UserSnapshotRepository;
import com.vention.curae.specialization.model.Specialization;
import com.vention.curae.specialization.repository.SpecializationRepository;
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
