package com.vention.curae.doctor.service;

import com.vention.curae.clinic.model.Clinic;
import com.vention.curae.clinic.service.ClinicService;
import com.vention.curae.doctor.dto.DoctorClinicAssignmentRequestDto;
import com.vention.curae.doctor.dto.DoctorResponseDto;
import com.vention.curae.doctor.dto.DoctorSearchDto;
import com.vention.curae.doctor.dto.DoctorSearchResponseDto;
import com.vention.curae.doctor.dto.DoctorUpdateDto;
import com.vention.curae.doctor.model.Doctor;
import com.vention.curae.doctor.repository.DoctorRepository;
import com.vention.curae.doctor.repository.specification.DoctorSpecification;
import com.vention.curae.event.UserProfileIdentitySyncRequestedEvent;
import com.vention.curae.provider.model.UserSnapshot;
import com.vention.curae.provider.repository.UserSnapshotRepository;
import com.vention.curae.specialization.model.Specialization;
import com.vention.curae.specialization.service.SpecializationService;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

	private final DoctorRepository doctorRepository;

	private final SpecializationService specializationService;

	private final ClinicService clinicService;

	private final UserSnapshotRepository userSnapshotRepository;

	private final KafkaTemplate<Object, Object> kafkaTemplate;

	@Override
	@Transactional(readOnly = true)
	public Page<DoctorSearchResponseDto> search(DoctorSearchDto searchDto, Pageable pageable) {
		return doctorRepository.findAll(DoctorSpecification.searchBySearchDto(searchDto), pageable)
			.map(this::toSearchResponse);
	}

	@Override
	@Transactional
	public DoctorResponseDto setClinics(Long id, DoctorClinicAssignmentRequestDto dto) {
		Doctor doctor = findDoctorById(id);
		updateClinics(doctor, dto.clinicIds());
		return toResponse(doctor);
	}

	@Override
	@Transactional(readOnly = true)
	public DoctorResponseDto getById(Long id) {
		return toResponse(findDoctorById(id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<DoctorResponseDto> getAll() {
		return doctorRepository.findAll().stream().map(this::toResponse).toList();
	}

	@Override
	@Transactional
	public DoctorResponseDto update(Long id, DoctorUpdateDto dto) {
		Doctor doctor = findDoctorById(id);
		UserSnapshot snapshot = requireSnapshot(doctor.getUserId());

		doctor.setBio(dto.bio());
		if (doctor.getDetails() != null) {
			doctor.getDetails().setPhoneNumber(dto.phoneNumber());
			doctor.getDetails().setLicenseNumber(dto.licenseNumber());
		}

		updateSpecializations(doctor, dto.specializationIds());
		updateClinics(doctor, dto.clinicIds());

		boolean identityChanged = !Objects.equals(snapshot.getFirstName(), dto.firstName())
				|| !Objects.equals(snapshot.getLastName(), dto.lastName());
		if (identityChanged) {
			snapshot.setFirstName(dto.firstName());
			snapshot.setLastName(dto.lastName());
			userSnapshotRepository.save(snapshot);
			UserProfileIdentitySyncRequestedEvent event = new UserProfileIdentitySyncRequestedEvent(
					snapshot.getUserId(), snapshot.getEmail(), snapshot.getFirstName(), snapshot.getLastName(),
					snapshot.getBirthDate() != null ? snapshot.getBirthDate().toString() : null,
					snapshot.getProfileUrl(), false, Instant.now());
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

		return toResponse(doctor);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		if (!doctorRepository.existsById(id)) {
			throw new EntityNotFoundException("Doctor not found");
		}
		doctorRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Doctor findDoctorById(Long id) {
		return doctorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Doctor not found"));
	}

	@Override
	@Transactional(readOnly = true)
	public Long findIdByUserId(Long userId) {
		return doctorRepository.findIdByUserId(userId).orElse(null);
	}

	private void updateSpecializations(Doctor doctor, Set<Long> specializationIds) {
		Set<Long> ids = specializationIds == null ? Set.of() : specializationIds;
		List<Specialization> found = specializationService.findAllByIds(ids);
		validateIds(ids, found.stream().map(Specialization::getId).collect(Collectors.toSet()), "Specializations");
		Set<Specialization> newSpecializations = new HashSet<>(found);

		doctor.getSpecializations()
			.stream()
			.filter(s -> !newSpecializations.contains(s))
			.toList()
			.forEach(doctor::removeSpecialization);
		newSpecializations.stream()
			.filter(s -> !doctor.getSpecializations().contains(s))
			.forEach(doctor::addSpecialization);
	}

	private void updateClinics(Doctor doctor, Set<Long> clinicIds) {
		Set<Long> ids = clinicIds == null ? Set.of() : clinicIds;
		List<Clinic> found = clinicService.findAllByIds(ids);
		validateIds(ids, found.stream().map(Clinic::getId).collect(Collectors.toSet()), "Clinics");
		Set<Clinic> newClinics = new HashSet<>(found);

		doctor.getClinics().stream().filter(c -> !newClinics.contains(c)).toList().forEach(doctor::removeClinic);
		newClinics.stream().filter(c -> !doctor.getClinics().contains(c)).forEach(doctor::addClinic);
	}

	private void validateIds(Set<Long> requested, Set<Long> found, String label) {
		if (requested.isEmpty()) {
			return;
		}
		List<Long> missing = requested.stream().filter(id -> !found.contains(id)).sorted().toList();
		if (!missing.isEmpty()) {
			throw new EntityNotFoundException(label + " not found with ids: " + missing);
		}
	}

	private UserSnapshot requireSnapshot(Long userId) {
		UserSnapshot snapshot = doctorSnapshot(userId);
		if (snapshot == null) {
			throw new EntityNotFoundException("User snapshot not found");
		}
		return snapshot;
	}

	private UserSnapshot doctorSnapshot(Long userId) {
		return userSnapshotRepository.findById(userId).orElse(null);
	}

	private DoctorResponseDto toResponse(Doctor doctor) {
		UserSnapshot snapshot = doctor.getUserSnapshot() != null ? doctor.getUserSnapshot()
				: doctorSnapshot(doctor.getUserId());
		return DoctorResponseDto.builder()
			.id(doctor.getId())
			.firstName(snapshot != null ? snapshot.getFirstName() : null)
			.lastName(snapshot != null ? snapshot.getLastName() : null)
			.bio(doctor.getBio())
			.email(snapshot != null ? snapshot.getEmail() : null)
			.licenseNumber(doctor.getDetails() != null ? doctor.getDetails().getLicenseNumber() : null)
			.phoneNumber(doctor.getDetails() != null ? doctor.getDetails().getPhoneNumber() : null)
			.specializations(doctor.getSpecializations()
				.stream()
				.sorted(Comparator.comparing(Specialization::getId, Comparator.nullsLast(Long::compareTo)))
				.map(s -> new com.vention.curae.specialization.dto.SpecializationBaseDto(s.getId(), s.getName(),
						s.getDescription()))
				.toList())
			.clinics(doctor.getClinics()
				.stream()
				.sorted(Comparator.comparing(Clinic::getId, Comparator.nullsLast(Long::compareTo)))
				.map(c -> new com.vention.curae.clinic.dto.ClinicBaseDto(c.getId(), c.getName(), c.getDescription(),
						c.getAddress() == null ? null
								: new com.vention.curae.address.dto.ClinicAddressBaseDto(c.getAddress().getStreet(),
										c.getAddress().getCity(), c.getAddress().getZipCode())))
				.toList())
			.build();
	}

	private DoctorSearchResponseDto toSearchResponse(Doctor doctor) {
		UserSnapshot snapshot = doctor.getUserSnapshot() != null ? doctor.getUserSnapshot()
				: doctorSnapshot(doctor.getUserId());
		String specialization = doctor.getSpecializations()
			.stream()
			.map(Specialization::getName)
			.sorted()
			.collect(Collectors.joining(", "));
		if (specialization.isBlank()) {
			specialization = null;
		}
		return DoctorSearchResponseDto.builder()
			.firstName(snapshot != null ? snapshot.getFirstName() : null)
			.lastName(snapshot != null ? snapshot.getLastName() : null)
			.profileUrl(snapshot != null ? snapshot.getProfileUrl() : null)
			.specialization(specialization)
			.build();
	}

}