package com.vention.medfast.doctor.service;

import com.vention.medfast.doctor.dto.DoctorProfileCreateDto;
import com.vention.medfast.doctor.mapper.DoctorMapper;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.repository.DoctorRepository;
import com.vention.medfast.doctor.util.DataUtils;
import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.specialization.service.SpecializationService;
import com.vention.medfast.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.times;

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

}
