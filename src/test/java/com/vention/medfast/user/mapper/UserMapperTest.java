package com.vention.medfast.user.mapper;

import com.vention.medfast.patient.model.Gender;
import com.vention.medfast.registration.dto.DoctorRegistrationRequestDto;
import com.vention.medfast.registration.dto.PersonDto;
import com.vention.medfast.user.model.RoleType;
import com.vention.medfast.user.model.User;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserMapperTest {

	private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

	private final PasswordEncoder passwordEncoder = new PasswordEncoder() {
		@Override
		public String encode(CharSequence rawPassword) {
			return "encoded-" + rawPassword;
		}

		@Override
		public boolean matches(CharSequence rawPassword, String encodedPassword) {
			return encodedPassword != null && encodedPassword.equals(encode(rawPassword));
		}
	};

	@Test
	void shouldMapPersonDtoToPatientUserWhenValidDataProvided() {
		PersonDto dto = new PersonDto("Jane", "Doe", LocalDate.of(1985, 5, 20), Gender.FEMALE, "password123",
				"jane.doe@mail.com", "Canada");

		User user = mapper.toPatientUser(dto, passwordEncoder);

		assertThat(user).isNotNull();
		assertThat(user.getId()).isNull();
		assertThat(user.getFirstName()).isEqualTo(dto.getName());
		assertThat(user.getLastName()).isEqualTo(dto.getLastName());
		assertThat(user.getEmail()).isEqualTo(dto.getEmail());
		assertThat(user.getBirthDate()).isEqualTo(dto.getBirthDate());
		assertThat(user.getRole()).isEqualTo(RoleType.PATIENT);
		assertThat(passwordEncoder.matches(dto.getRawPassword(), user.getPasswordHash())).isTrue();
	}

	@Test
	void shouldReturnNullWhenPersonDtoIsNull() {
		assertThat(mapper.toPatientUser(null, passwordEncoder)).isNull();
	}

	@Test
	void shouldMapUserCreateDtoToDoctorUserWhenValidDataProvided() {
		DoctorRegistrationRequestDto dto = DoctorRegistrationRequestDto.builder()
			.firstName("John")
			.lastName("Doe")
			.email("john@med.com")
			.phoneNumber("+1123456789")
			.birthDate(LocalDate.of(2000, 1, 1))
			.specializationIds(Set.of(1L))
			.licenseNumber("1234567")
			.build();
		String tempPassword = "1234567";

		User user = mapper.toDoctorUser(dto, tempPassword, passwordEncoder);

		assertThat(user).isNotNull();
		assertThat(user.getId()).isNull();
		assertThat(user.getFirstName()).isEqualTo(dto.firstName());
		assertThat(user.getLastName()).isEqualTo(dto.lastName());
		assertThat(user.getEmail()).isEqualTo(dto.email());
		assertThat(user.getBirthDate()).isEqualTo(dto.birthDate());
		assertThat(user.getRole()).isEqualTo(RoleType.DOCTOR);
		assertThat(passwordEncoder.matches(tempPassword, user.getPasswordHash())).isTrue();
	}

	@Test
	void shouldReturnNullWhenUserCreateDtoIsNull() {
		assertThat(mapper.toDoctorUser(null, null, passwordEncoder)).isNull();
	}

}
