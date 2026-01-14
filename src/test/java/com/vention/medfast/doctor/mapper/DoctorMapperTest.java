package com.vention.medfast.doctor.mapper;

import com.vention.medfast.address.model.Address;
import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.doctor.dto.DoctorResponseDto;
import com.vention.medfast.doctor.dto.DoctorSearchResponseDto;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.model.DoctorDetails;
import com.vention.medfast.doctor.util.DataUtils;
import com.vention.medfast.registration.dto.DoctorRegistrationRequestDto;
import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.user.model.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
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

		DoctorRegistrationRequestDto dto = DoctorRegistrationRequestDto.builder()
			.firstName("John")
			.lastName("Doe")
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

	@Test
	void shouldMapToDoctorResponseDtoWithFullGraph() {
		Doctor doctor = DataUtils.getDoctorPersisted();

		Clinic clinic = new Clinic();
		clinic.setId(50L);
		clinic.setName("City Clinic");
		Address address = new Address();
		address.setStreet("Baker St");
		clinic.setAddress(address);

		doctor.addClinic(clinic);

		DoctorResponseDto res = mapper.toDoctorResponseDto(doctor);

		assertThat(res.id()).isEqualTo(DataUtils.DOCTOR_ID);
		assertThat(res.firstName()).isEqualTo(DataUtils.FIRST_NAME);
		assertThat(res.licenseNumber()).isEqualTo(DataUtils.LICENSE_NUMBER);

		assertThat(res.specializations()).hasSize(1);
		assertThat(res.specializations().getFirst().name()).isEqualTo(DataUtils.SPECIALIZATION_NAME);

		assertThat(res.clinics()).hasSize(1);
		assertThat(res.clinics().getFirst().name()).isEqualTo("City Clinic");
		assertThat(res.clinics().getFirst().address().street()).isEqualTo("Baker St");
	}

	@Test
	void shouldReturnNullWhenInputsAreNull() {
		assertThat(mapper.toDoctor(null, null, null)).isNull();
		assertThat(mapper.toDoctorDetails(null)).isNull();
		assertThat(mapper.toDoctorResponseDto(null)).isNull();
		assertThat(mapper.toSpecializationBaseDto(null)).isNull();
		assertThat(mapper.toClinicBaseDto(null)).isNull();
		assertThat(mapper.toAddressDto(null)).isNull();

		mapper.updateDoctorFromDto(null, new Doctor());
		mapper.updateUserFromDto(null, new User());
		mapper.updateDetailsFromDto(null, new DoctorDetails());

		Doctor d = new Doctor();
		mapper.linkSpecializationsAndGenerateBio(d, null);
		assertThat(d.getSpecializations()).isEmpty();
	}

	@Test
	void shouldMapToDoctorSearchResponseDtoWheInputIsValid() {
		User user = new User();
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setProfileUrl("http://example.com/photo.jpg");

		Specialization spec1 = new Specialization();
		spec1.setName("Cardiology");
		Specialization spec2 = new Specialization();
		spec2.setName("Neurology");

		Doctor doctor = new Doctor();
		doctor.setUser(user);
		doctor.setSpecializations(Set.of(spec1, spec2));

		DoctorSearchResponseDto result = mapper.toDoctorSearchResponseDto(doctor);

		assertThat(result).isNotNull();
		assertThat(result.firstName()).isEqualTo("John");
		assertThat(result.lastName()).isEqualTo("Doe");
		assertThat(result.profileUrl()).isEqualTo("http://example.com/photo.jpg");
		assertThat(result.specialization()).contains("Cardiology", "Neurology");
	}

	@Test
	void shouldReturnNullWhenSpecializationsAreEmptyOrNull() {
		String nullResult = mapper.specializationsToString(null);
		String emptyResult = mapper.specializationsToString(Collections.emptySet());

		assertThat(nullResult).isNull();
		assertThat(emptyResult).isNull();
	}

}
