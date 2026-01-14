package com.vention.medfast.doctor.service;

import com.vention.medfast.doctor.dto.DoctorClinicAssignmentRequestDto;
import com.vention.medfast.doctor.mapper.DoctorMapper;
import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.clinic.service.ClinicService;
import com.vention.medfast.doctor.dto.DoctorResponseDto;
import com.vention.medfast.doctor.dto.DoctorSearchDto;
import com.vention.medfast.doctor.dto.DoctorUpdateDto;
import com.vention.medfast.doctor.dto.DoctorSearchResponseDto;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.repository.DoctorRepository;
import com.vention.medfast.doctor.repository.specification.DoctorSpecification;
import com.vention.medfast.registration.dto.DoctorRegistrationRequestDto;
import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.specialization.service.SpecializationService;
import com.vention.medfast.user.model.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

	private final DoctorMapper doctorMapper;

	private final DoctorRepository doctorRepository;

	private final SpecializationService specializationService;

	private final ClinicService clinicService;

	@Override
	@Transactional
	public Doctor createProfile(User user, DoctorRegistrationRequestDto dto) {
		List<Specialization> specializations = specializationService.findAllByIds(dto.specializationIds());

		Doctor doctor = doctorMapper.toDoctor(user, dto, specializations);

		return doctorRepository.save(doctor);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<DoctorSearchResponseDto> search(DoctorSearchDto searchDto, Pageable pageable) {
		return doctorRepository.findAll(DoctorSpecification.searchBySearchDto(searchDto), pageable)
			.map(doctorMapper::toDoctorSearchResponseDto);
	}

	@Override
	@Transactional
	public DoctorResponseDto setClinics(Long id, DoctorClinicAssignmentRequestDto dto) {
		Doctor doctor = doctorRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

		updateClinics(doctor, dto.clinicIds());

		return doctorMapper.toDoctorResponseDto(doctor);
	}

	@Override
	@Transactional(readOnly = true)
	public DoctorResponseDto getById(Long id) {
		Doctor doctor = doctorRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

		return doctorMapper.toDoctorResponseDto(doctor);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DoctorResponseDto> getAll() {
		List<Doctor> doctors = doctorRepository.findAll();

		return doctors.stream().map(doctorMapper::toDoctorResponseDto).toList();
	}

	@Override
	@Transactional
	public DoctorResponseDto update(Long id, DoctorUpdateDto dto) {
		Doctor doctor = doctorRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

		doctorMapper.updateDoctorFromDto(dto, doctor);
		doctorMapper.updateUserFromDto(dto, doctor.getUser());
		doctorMapper.updateDetailsFromDto(dto, doctor.getDetails());

		updateSpecializations(doctor, dto.specializationIds());
		updateClinics(doctor, dto.clinicIds());

		return doctorMapper.toDoctorResponseDto(doctor);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		if (!doctorRepository.existsById(id)) {
			throw new EntityNotFoundException("Doctor not found");
		}

		doctorRepository.deleteById(id);
	}

	private void updateSpecializations(Doctor doctor, Set<Long> specializationsIds) {
		Set<Specialization> newSpecializations = new HashSet<>(specializationService.findAllByIds(specializationsIds));

		doctor.getSpecializations()
			.stream()
			.filter(specialization -> !newSpecializations.contains(specialization))
			.toList()
			.forEach(doctor::removeSpecialization);

		newSpecializations.stream()
			.filter(specialization -> !doctor.getSpecializations().contains(specialization))
			.forEach(doctor::addSpecialization);
	}

	private void updateClinics(Doctor doctor, Set<Long> clinicIds) {
		Set<Clinic> newClinics = new HashSet<>(clinicService.findAllByIds(clinicIds));

		doctor.getClinics()
			.stream()
			.filter(clinic -> !newClinics.contains(clinic))
			.toList()
			.forEach(doctor::removeClinic);

		newClinics.stream().filter(clinic -> !doctor.getClinics().contains(clinic)).forEach(doctor::addClinic);
	}

	@Override
	public Doctor findDoctorById(Long id) {
		return doctorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Doctor not found"));
	}

}
