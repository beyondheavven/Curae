package com.vention.medfast.doctor.service;

import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.clinic.service.ClinicService;
import com.vention.medfast.doctor.dto.DoctorClinicAssignmentRequestDto;
import com.vention.medfast.doctor.dto.DoctorResponseDto;
import com.vention.medfast.doctor.dto.DoctorSearchDto;
import com.vention.medfast.doctor.dto.DoctorUpdateDto;
import com.vention.medfast.doctor.dto.DoctorSearchResponseDto;
import com.vention.medfast.doctor.mapper.DoctorMapper;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.repository.DoctorRepository;
import com.vention.medfast.doctor.util.DataUtils;
import com.vention.medfast.registration.dto.DoctorRegistrationRequestDto;
import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.specialization.service.SpecializationService;
import com.vention.medfast.user.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceImplTest {

	@Mock
	private DoctorRepository doctorRepository;

	@Mock
	private SpecializationService specializationService;

	@Mock
	private ClinicService clinicService;

	@Mock
	private DoctorMapper doctorMapper;

	@InjectMocks
	private DoctorServiceImpl service;

	@Test
	void shouldCreateDoctorProfileWhenValidRequest() {
		User user = DataUtils.getDoctorUserPersisted();
		DoctorRegistrationRequestDto requestDto = DataUtils.getDoctorRegistrationRequestDto();

		Doctor mappedDoctor = DataUtils.getDoctorPersisted();

		Doctor savedDoctor = DataUtils.getDoctorPersisted();

		Specialization specializationCardiology = DataUtils.getSpecializationCardiology();
		List<Specialization> specializations = List.of(specializationCardiology);

		given(specializationService.findAllByIds(requestDto.specializationIds())).willReturn(specializations);

		given(doctorMapper.toDoctor(user, requestDto, specializations)).willReturn(mappedDoctor);

		given(doctorRepository.save(any(Doctor.class))).willReturn(savedDoctor);

		Doctor createdDoctor = service.createProfile(user, requestDto);

		assertThat(createdDoctor).isNotNull();
		assertThat(createdDoctor.getId()).isEqualTo(DataUtils.DOCTOR_ID);
		assertThat(createdDoctor.getUser().getEmail()).isEqualTo(DataUtils.EMAIL);
		assertThat(createdDoctor.getUser().getFirstName()).isEqualTo(DataUtils.FIRST_NAME);
		assertThat(createdDoctor.getUser().getLastName()).isEqualTo(DataUtils.LAST_NAME);
		assertThat(createdDoctor.getDetails().getLicenseNumber()).isEqualTo(DataUtils.LICENSE_NUMBER);
		assertThat(createdDoctor.getDetails().getPhoneNumber()).isEqualTo(DataUtils.PHONE_NUMBER);
		assertThat(createdDoctor.getSpecializations()).hasSize(1);
		assertThat(createdDoctor.getSpecializations()).contains(specializationCardiology);

		verify(doctorRepository, times(1)).save(any(Doctor.class));
	}

	@Test
	void shouldReturnPagedResultsWhenSearchQueryIsValid() {
		String query = "House";
		DoctorSearchDto searchDto = new DoctorSearchDto(query, null, null);
		Pageable pageable = PageRequest.of(0, 10);

		Doctor doctor = new Doctor();
		doctor.setId(1L);
		Page<Doctor> doctorPage = new PageImpl<>(List.of(doctor), pageable, 1);

		DoctorSearchResponseDto responseDto = DoctorSearchResponseDto.builder()
			.firstName("Gregory")
			.lastName("House")
			.specialization("Diagnostician")
			.build();

		given(doctorRepository.findAll(any(Specification.class), eq(pageable))).willReturn(doctorPage);
		given(doctorMapper.toDoctorSearchResponseDto(doctor)).willReturn(responseDto);

		Page<DoctorSearchResponseDto> result = service.search(searchDto, pageable);

		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().getFirst().lastName()).isEqualTo("House");

		verify(doctorRepository).findAll(any(Specification.class), eq(pageable));
		verify(doctorMapper).toDoctorSearchResponseDto(doctor);
	}

	@Test
	void shouldReturnEmptyPageWhenNoDoctorsMatchQuery() {
		DoctorSearchDto searchDto = new DoctorSearchDto("Unknown", null, null);
		Pageable pageable = PageRequest.of(0, 10);
		Page<Doctor> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

		given(doctorRepository.findAll(any(Specification.class), eq(pageable))).willReturn(emptyPage);

		Page<DoctorSearchResponseDto> result = service.search(searchDto, pageable);

		assertThat(result).isNotNull();
		assertThat(result.isEmpty()).isTrue();

		verify(doctorRepository).findAll(any(Specification.class), eq(pageable));
		verifyNoInteractions(doctorMapper);
	}

	@Test
	void shouldReturnDoctorsWhenSearchBySpecializationAndName() {
		DoctorSearchDto searchDto = new DoctorSearchDto(DataUtils.FIRST_NAME, Set.of(10L), null);
		Pageable pageable = PageRequest.of(0, 10);

		Doctor doctor = DataUtils.getDoctorPersisted();
		Page<Doctor> doctorPage = new PageImpl<>(List.of(doctor));

		DoctorSearchResponseDto responseDto = DoctorSearchResponseDto.builder()
			.firstName(DataUtils.FIRST_NAME)
			.lastName(DataUtils.LAST_NAME)
			.build();

		given(doctorRepository.findAll(any(Specification.class), eq(pageable))).willReturn(doctorPage);
		given(doctorMapper.toDoctorSearchResponseDto(doctor)).willReturn(responseDto);

		Page<DoctorSearchResponseDto> result = service.search(searchDto, pageable);

		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().getFirst()).isEqualTo(responseDto);

		verify(doctorRepository).findAll(any(Specification.class), eq(pageable));
	}

	@Test
	void shouldReturnDoctorsByClinicOnly() {
		DoctorSearchDto searchDto = new DoctorSearchDto(null, null, Set.of(20L));
		Pageable pageable = PageRequest.of(0, 10);

		Doctor doctor = DataUtils.getDoctorPersisted();
		Page<Doctor> doctorPage = new PageImpl<>(List.of(doctor));

		DoctorSearchResponseDto responseDto = DoctorSearchResponseDto.builder()
			.firstName("Clinic")
			.lastName("Doctor")
			.build();

		given(doctorRepository.findAll(any(Specification.class), eq(pageable))).willReturn(doctorPage);
		given(doctorMapper.toDoctorSearchResponseDto(doctor)).willReturn(responseDto);

		Page<DoctorSearchResponseDto> result = service.search(searchDto, pageable);

		assertThat(result.getContent()).hasSize(1);
		verify(doctorRepository).findAll(any(Specification.class), eq(pageable));
	}

	@Test
	void shouldAssignClinicsToDoctorWhenValidRequest() {
		Long doctorId = DataUtils.DOCTOR_ID;
		Set<Long> clinicIds = Set.of(10L, 20L);
		DoctorClinicAssignmentRequestDto requestDto = new DoctorClinicAssignmentRequestDto(clinicIds);

		Doctor doctor = DataUtils.getDoctorPersisted();
		doctor.setClinics(new HashSet<>());

		Clinic clinic1 = new Clinic();
		clinic1.setId(10L);
		clinic1.setName("Clinic A");
		Clinic clinic2 = new Clinic();
		clinic2.setId(20L);
		clinic2.setName("Clinic B");
		List<Clinic> foundClinics = List.of(clinic1, clinic2);

		DoctorResponseDto responseDto = DataUtils.getDoctorResponseDto();

		given(doctorRepository.findById(doctorId)).willReturn(Optional.of(doctor));
		given(clinicService.findAllByIds(clinicIds)).willReturn(foundClinics);
		given(doctorMapper.toDoctorResponseDto(doctor)).willReturn(responseDto);

		DoctorResponseDto result = service.setClinics(doctorId, requestDto);

		assertThat(result).isNotNull();
		assertThat(doctor.getClinics()).hasSize(2);
		assertThat(doctor.getClinics()).contains(clinic1, clinic2);

		verify(clinicService).findAllByIds(clinicIds);
		verify(doctorMapper).toDoctorResponseDto(doctor);
	}

	@Test
	void shouldThrowExceptionWhenAssignClinicsToNonExistentDoctor() {
		DoctorClinicAssignmentRequestDto requestDto = new DoctorClinicAssignmentRequestDto(Set.of(1L));

		given(doctorRepository.findById(999L)).willReturn(Optional.empty());

		assertThatThrownBy(() -> service.setClinics(999L, requestDto)).isInstanceOf(EntityNotFoundException.class)
			.hasMessageContaining("Doctor not found");

		verify(clinicService, never()).findAllByIds(any());
	}

	@Test
	void shouldGetDoctorByIdWhenDoctorExists() {
		Doctor doctor = DataUtils.getDoctorPersisted();
		DoctorResponseDto responseDto = DataUtils.getDoctorResponseDto();

		given(doctorRepository.findById(DataUtils.DOCTOR_ID)).willReturn(Optional.of(doctor));
		given(doctorMapper.toDoctorResponseDto(doctor)).willReturn(responseDto);

		DoctorResponseDto result = service.getById(DataUtils.DOCTOR_ID);

		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(DataUtils.DOCTOR_ID);
		assertThat(result.email()).isEqualTo(DataUtils.EMAIL);

		verify(doctorRepository).findById(DataUtils.DOCTOR_ID);
	}

	@Test
	void shouldThrowExceptionWhenGetDoctorByIdNotFound() {
		given(doctorRepository.findById(99L)).willReturn(Optional.empty());

		assertThatThrownBy(() -> service.getById(99L)).isInstanceOf(EntityNotFoundException.class)
			.hasMessageContaining("Doctor not found");
	}

	@Test
	void shouldGetAllDoctors() {
		List<Doctor> doctors = List.of(DataUtils.getDoctorPersisted());
		DoctorResponseDto responseDto = DataUtils.getDoctorResponseDto();

		given(doctorRepository.findAll()).willReturn(doctors);
		given(doctorMapper.toDoctorResponseDto(any(Doctor.class))).willReturn(responseDto);

		List<DoctorResponseDto> result = service.getAll();

		assertThat(result).hasSize(1);

		verify(doctorRepository).findAll();
	}

	@Test
	void shouldUpdateDoctorWhenValidRequest() {
		Long doctorId = DataUtils.DOCTOR_ID;
		Doctor existingDoctor = DataUtils.getDoctorPersisted();
		DoctorUpdateDto updateDto = DataUtils.getDoctorUpdateDto();

		Specialization newSpecialization = new Specialization();
		newSpecialization.setId(10L);
		newSpecialization.setName("Dentist");
		List<Specialization> newSpecializations = List.of(newSpecialization);

		Clinic newClinic = new Clinic();
		newClinic.setId(20L);
		newClinic.setName("City Clinic");
		List<Clinic> newClinics = List.of(newClinic);

		given(doctorRepository.findById(doctorId)).willReturn(Optional.of(existingDoctor));
		given(specializationService.findAllByIds(updateDto.specializationIds())).willReturn(newSpecializations);
		given(clinicService.findAllByIds(updateDto.clinicIds())).willReturn(newClinics);

		DoctorResponseDto updatedResponse = DataUtils.getDoctorResponseDto();
		given(doctorMapper.toDoctorResponseDto(existingDoctor)).willReturn(updatedResponse);

		DoctorResponseDto result = service.update(doctorId, updateDto);

		verify(doctorMapper).updateDoctorFromDto(updateDto, existingDoctor);
		verify(doctorMapper).updateUserFromDto(updateDto, existingDoctor.getUser());
		verify(doctorMapper).updateDetailsFromDto(updateDto, existingDoctor.getDetails());

		assertThat(existingDoctor.getSpecializations()).hasSize(1);
		assertThat(existingDoctor.getSpecializations()).contains(newSpecialization);
		assertThat(existingDoctor.getClinics()).hasSize(1);
		assertThat(existingDoctor.getClinics()).contains(newClinic);
	}

	@Test
	void shouldThrowExceptionWhenUpdateDoctorNotFound() {
		DoctorUpdateDto updateDto = DataUtils.getDoctorUpdateDto();
		given(doctorRepository.findById(99L)).willReturn(Optional.empty());

		assertThatThrownBy(() -> service.update(99L, updateDto)).isInstanceOf(EntityNotFoundException.class);

		verify(doctorRepository, never()).save(any());
	}

	@Test
	void shouldDeleteDoctorWhenExists() {
		Long doctorId = DataUtils.DOCTOR_ID;
		given(doctorRepository.existsById(doctorId)).willReturn(true);

		service.delete(doctorId);

		verify(doctorRepository).deleteById(doctorId);
	}

	@Test
	void shouldThrowExceptionWhenDeleteDoctorNotFound() {
		given(doctorRepository.existsById(anyLong())).willReturn(false);

		assertThatThrownBy(() -> service.delete(anyLong())).isInstanceOf(EntityNotFoundException.class);

		verify(doctorRepository, never()).deleteById(anyLong());
	}

}
