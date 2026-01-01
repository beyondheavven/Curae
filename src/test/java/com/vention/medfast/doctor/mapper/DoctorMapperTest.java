package com.vention.medfast.doctor.mapper;

import com.vention.medfast.doctor.dto.DoctorProfileCreateDto;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.user.model.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DoctorMapperTest {

	private final DoctorMapper mapper = Mappers.getMapper(DoctorMapper.class);

	@Test
	void shouldMapToDoctorAndLinkSpecializationsWhenValidDto() {
		User user = new User();
		user.setId(1L);
		user.setEmail("doctor@med.com");

		DoctorProfileCreateDto dto = DoctorProfileCreateDto.builder()
			.licenseNumber("1234567")
			.phoneNumber("+1123456789")
			.specializationIds(Set.of(1L, 2L))
			.build();

		Specialization specialization = new Specialization();
		specialization.setId(1L);
		specialization.setName("Cardiologist");

		Specialization specialization2 = new Specialization();
		specialization2.setId(2L);
		specialization2.setName("Dentist");

		List<Specialization> specializations = List.of(specialization, specialization2);

		Doctor doctor = mapper.toDoctor(user, dto, specializations);

		assertThat(doctor).isNotNull();
		assertThat(doctor.getUser()).isEqualTo(user);

		assertThat(doctor.getDetails()).isNotNull();
		assertThat(doctor.getDetails().getLicenseNumber()).isEqualTo("1234567");
		assertThat(doctor.getDetails().getPhoneNumber()).isEqualTo("+1123456789");
		assertThat(doctor.getDetails().getDoctor()).isEqualTo(doctor);

		assertThat(doctor.getSpecializations()).hasSize(2);
		assertThat(doctor.getSpecializations()).contains(specialization, specialization2);

		assertThat(specialization.getDoctors()).contains(doctor);
		assertThat(specialization2.getDoctors()).contains(doctor);
	}

}
