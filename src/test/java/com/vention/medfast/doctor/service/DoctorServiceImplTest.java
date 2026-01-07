package com.vention.medfast.doctor.service;

import com.vention.medfast.doctor.dto.DoctorProfileCreateDto;
import com.vention.medfast.doctor.dto.DoctorSearchResponseDto;
import com.vention.medfast.doctor.mapper.DoctorMapper;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.repository.DoctorRepository;
import com.vention.medfast.doctor.util.DataUtils;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.times;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceImplTest {

	@Mock
	private DoctorRepository doctorRepository;

	@Mock
	private SpecializationService specializationService;

	@Mock
	private DoctorMapper doctorMapper;

	@InjectMocks
	private DoctorServiceImpl service;

	@Test
	void shouldCreateDoctorProfileWhenValidRequest() {
		User user = DataUtils.getDoctorUserPersisted();
		DoctorProfileCreateDto requestDto = DataUtils.getDoctorProfileCreateRequestDto();

		Doctor mappedDoctor = DataUtils.getDoctorPersisted();

		Doctor savedDoctor = DataUtils.getDoctorPersisted();

		Specialization specializationCardiology = DataUtils.getSpecializationCardiology();
		List<Specialization> specializations = List.of(specializationCardiology);

		given(specializationService.findAllByIds(requestDto.specializationIds())).willReturn(specializations);

		given(doctorMapper.toDoctor(user, requestDto, specializations)).willReturn(mappedDoctor);

		given(doctorRepository.save(any(Doctor.class))).willReturn(savedDoctor);

		Doctor createdDoctor = service.createDoctorProfile(user, requestDto);

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
	void shouldReturnDoctorWhenIdExists() {
		Long doctorId = 1L;
		Doctor expectedDoctor = new Doctor();
		expectedDoctor.setId(doctorId);

		given(doctorRepository.findById(doctorId)).willReturn(Optional.of(expectedDoctor));

		Doctor result = service.getById(doctorId);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(doctorId);
		verify(doctorRepository).findById(doctorId);
	}

	@Test
	void shouldThrowExceptionWhenDoctorNotFound() {
		Long nonExistentId = 999L;
		given(doctorRepository.findById(nonExistentId)).willReturn(Optional.empty());

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			service.getById(nonExistentId);
		});

		assertThat(exception.getMessage()).isEqualTo("Doctor not found");
		verify(doctorRepository).findById(nonExistentId);
	}

	@Test
	void shouldReturnPagedResultsWhenSearchQueryIsValid() {
		String query = "House";
		Pageable pageable = PageRequest.of(0, 10);

		Doctor doctor = new Doctor();
		doctor.setId(1L);
		List<Doctor> doctorList = List.of(doctor);
		Page<Doctor> doctorPage = new PageImpl<>(doctorList, pageable, 1);

		DoctorSearchResponseDto responseDto = DoctorSearchResponseDto.builder()
			.firstName("Gregory")
			.lastName("House")
			.build();

		given(doctorRepository.findAll(any(Specification.class), eq(pageable))).willReturn(doctorPage);
		given(doctorMapper.toDoctorSearchResponseDto(doctor)).willReturn(responseDto);

		Page<DoctorSearchResponseDto> result = service.search(query, pageable);

		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).lastName()).isEqualTo("House");

		verify(doctorRepository).findAll(any(Specification.class), eq(pageable));
		verify(doctorMapper).toDoctorSearchResponseDto(doctor);
	}

	@Test
	void shouldReturnEmptyPageWhenNoDoctorsMatchQuery() {
		String query = "Unknown";
		Pageable pageable = PageRequest.of(0, 10);
		Page<Doctor> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

		given(doctorRepository.findAll(any(Specification.class), eq(pageable))).willReturn(emptyPage);

		Page<DoctorSearchResponseDto> result = service.search(query, pageable);

		assertThat(result).isNotNull();
		assertThat(result.isEmpty()).isTrue();

		verify(doctorRepository).findAll(any(Specification.class), eq(pageable));
		verifyNoInteractions(doctorMapper);
	}

}
