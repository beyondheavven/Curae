package com.vention.medfast.patient.mapper;

import com.vention.medfast.address.dto.AddressCreateDto;
import com.vention.medfast.patient.dto.PatientCreateDto;
import com.vention.medfast.patient.dto.PatientResponseDto;
import com.vention.medfast.patient.model.GenderType;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.user.dto.UserCreateDto;
import com.vention.medfast.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class PatientMapperTest {

	private PatientMapper patientMapper;

	@BeforeEach
	void setUp() {
		patientMapper = Mappers.getMapper(PatientMapper.class);
	}

	@Test
	void shouldMapEntityToDtoWhenInputIsValid() {
		User user = new User();
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setProfileUrl("http://profile.url");

		Patient entity = new Patient();
		entity.setId(1L);
		entity.setUser(user);
		entity.setCitizenship("USA");

		PatientResponseDto result = patientMapper.toDto(entity);

		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(1L);
		assertThat(result.firstName()).isEqualTo("John");
		assertThat(result.lastName()).isEqualTo("Doe");
		assertThat(result.profileUrl()).isEqualTo("http://profile.url");
	}

	@Test
	void shouldMapDtoToEntityWhenInputIsValid() {
		UserCreateDto userDto = new UserCreateDto("Jane", "Smith", "jane@example.com", "pass", "http://img.com");
		AddressCreateDto addressDto = new AddressCreateDto("Street", "City", "123", "State", "1", "00000", "+123456");

		PatientCreateDto dto = PatientCreateDto.builder()
			.user(userDto)
			.address(addressDto)
			.citizenship("Canada")
			.gender(GenderType.FEMALE)
			.birthDate(LocalDate.of(1995, 5, 20))
			.build();

		Patient result = patientMapper.toEntity(dto);

		assertThat(result).isNotNull();
		assertThat(result.getCitizenship()).isEqualTo("Canada");
		assertThat(result.getGender()).isEqualTo(GenderType.FEMALE);
		assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(1995, 5, 20));

		if (result.getUser() != null) {
			assertThat(result.getUser().getFirstName()).isEqualTo("Jane");
		}
	}

	@Test
	void shouldReturnNullWhenEntityIsNull() {
		assertThat(patientMapper.toDto(null)).isNull();
	}

	@Test
	void shouldReturnNullWhenDtoIsNull() {
		assertThat(patientMapper.toEntity(null)).isNull();
	}

}
