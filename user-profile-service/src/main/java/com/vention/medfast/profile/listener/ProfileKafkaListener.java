package com.vention.medfast.profile.listener;

import com.vention.medfast.address.model.Address;
import com.vention.medfast.address.repository.AddressRepository;
import com.vention.medfast.event.PatientRegistrationEvent;
import com.vention.medfast.event.UserIdentityUpdatedEvent;
import com.vention.medfast.patient.model.Gender;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.repository.PatientRepository;
import com.vention.medfast.profile.model.UserSnapshot;
import com.vention.medfast.profile.repository.UserSnapshotRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileKafkaListener {

	private final PatientRepository patientRepository;

	private final AddressRepository addressRepository;

	private final UserSnapshotRepository userSnapshotRepository;

	@Transactional
	@KafkaListener(topics = "user.patient-profile.registered", groupId = "user-profile-service-group")
	public void handlePatientRegistered(PatientRegistrationEvent event) {
		log.info("Received PatientRegistrationEvent for userId={}", event.userId());

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

		if (patientRepository.existsByUserId(event.userId())) {
			return;
		}

		Address address = new Address();
		if (event.address() != null) {
			address.setStreet(event.address().street());
			address.setCity(event.address().city());
			address.setApartment(event.address().apartment());
			address.setHouseNumber(event.address().houseNumber());
			address.setState(event.address().state());
			address.setZipCode(event.address().zipCode());
			address.setPhone(event.address().phoneNumber());
		}
		address = addressRepository.save(address);

		Patient patient = new Patient();
		patient.setUserId(event.userId());
		patient.setCitizenship(event.citizenship());
		if (event.gender() != null) {
			patient.setGender(Gender.valueOf(event.gender()));
		}
		patient.setAddress(address);
		patientRepository.save(patient);
	}

	@Transactional
	@KafkaListener(topics = "user.identity.updated", groupId = "user-profile-service-group")
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
