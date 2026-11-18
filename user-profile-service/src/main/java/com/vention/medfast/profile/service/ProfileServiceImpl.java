package com.vention.medfast.profile.service;

import com.vention.medfast.address.model.Address;
import com.vention.medfast.event.UserProfileIdentitySyncRequestedEvent;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.repository.PatientRepository;
import com.vention.medfast.profile.dto.request.ProfileRequestDto;
import com.vention.medfast.profile.dto.response.ContactsDto;
import com.vention.medfast.profile.dto.response.PersonalDetailsDto;
import com.vention.medfast.profile.dto.response.ProfileAddressDto;
import com.vention.medfast.profile.dto.response.ProfileResponseDto;
import com.vention.medfast.profile.model.UserSnapshot;
import com.vention.medfast.profile.repository.UserSnapshotRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

	private final PatientRepository patientRepository;

	private final UserSnapshotRepository userSnapshotRepository;

	private final KafkaTemplate<Object, Object> kafkaTemplate;

	@Override
	@Transactional(readOnly = true)
	public ProfileResponseDto getProfile(Long userId) {
		Patient patient = patientRepository.findByUserId(userId)
			.orElseThrow(() -> new EntityNotFoundException("Patient profile not found"));
		UserSnapshot snapshot = userSnapshotRepository.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException("User snapshot not found"));
		Address address = patient.getAddress();

		PersonalDetailsDto personal = new PersonalDetailsDto(snapshot.getFirstName(), snapshot.getLastName(),
				snapshot.getBirthDate(), patient.getGender(), patient.getCitizenship(), snapshot.getProfileUrl());
		ProfileAddressDto addr = new ProfileAddressDto(address.getState(), address.getCity(), address.getStreet(),
				address.getZipCode(), address.getHouseNumber(), address.getApartment());
		ContactsDto contacts = new ContactsDto(address.getPhone(), snapshot.getEmail());

		return new ProfileResponseDto(personal, addr, contacts, snapshot.getStatus());
	}

	@Override
	@Transactional
	public void updateProfile(Long userId, ProfileRequestDto dto) {
		Patient patient = patientRepository.findByUserId(userId)
			.orElseThrow(() -> new EntityNotFoundException("Patient profile not found"));
		UserSnapshot snapshot = userSnapshotRepository.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException("User snapshot not found"));
		Address address = patient.getAddress();

		String oldEmail = snapshot.getEmail();
		String oldFirstName = snapshot.getFirstName();
		String oldLastName = snapshot.getLastName();
		var oldBirthDate = snapshot.getBirthDate();
		String oldProfileUrl = snapshot.getProfileUrl();

		if (dto.getPersonalDetailsUpdateDto() != null) {
			var p = dto.getPersonalDetailsUpdateDto();
			snapshot.setFirstName(p.getName());
			snapshot.setLastName(p.getLastName());
			snapshot.setBirthDate(p.getBirthDate());
			snapshot.setProfileUrl(p.getPhotoUrl());
			patient.setGender(p.getGender());
			patient.setCitizenship(p.getCitizenship());
		}

		if (dto.getProfileAddressUpdateDto() != null) {
			var a = dto.getProfileAddressUpdateDto();
			address.setStreet(a.getStreet());
			address.setCity(a.getCity());
			address.setApartment(a.getApartment());
			address.setHouseNumber(a.getHouseNumber());
			address.setState(a.getState());
			address.setZipCode(a.getZipCode());
		}

		boolean emailChanged = false;
		if (dto.getContactsUpdateDto() != null) {
			var c = dto.getContactsUpdateDto();
			address.setPhone(c.getPhone());
			if (c.getEmail() != null && !c.getEmail().trim().equalsIgnoreCase(snapshot.getEmail())) {
				snapshot.setEmail(c.getEmail().trim().toLowerCase());
				snapshot.setStatus("UNVERIFIED");
				emailChanged = true;
			}
		}

		boolean identityChanged = !Objects.equals(oldFirstName, snapshot.getFirstName())
				|| !Objects.equals(oldLastName, snapshot.getLastName())
				|| !Objects.equals(oldBirthDate, snapshot.getBirthDate())
				|| !Objects.equals(oldProfileUrl, snapshot.getProfileUrl());

		if (identityChanged || emailChanged) {
			UserProfileIdentitySyncRequestedEvent event = new UserProfileIdentitySyncRequestedEvent(userId,
					snapshot.getEmail(), snapshot.getFirstName(), snapshot.getLastName(),
					snapshot.getBirthDate() != null ? snapshot.getBirthDate().toString() : null,
					snapshot.getProfileUrl(), emailChanged, Instant.now());
			if (TransactionSynchronizationManager.isSynchronizationActive()) {
				TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
					@Override
					public void afterCommit() {
						kafkaTemplate.send("user.profile.identity-sync-requested", event);
					}
				});
			}
			else {
				kafkaTemplate.send("user.profile.identity-sync-requested", event);
			}
		}
	}

}
