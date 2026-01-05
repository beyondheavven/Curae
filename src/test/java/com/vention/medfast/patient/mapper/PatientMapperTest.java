package com.vention.medfast.patient.mapper;

import com.vention.medfast.address.dto.AddressCreateDto;
import com.vention.medfast.address.model.Address;
import com.vention.medfast.patient.dto.PatientCreateDto;
import com.vention.medfast.patient.dto.PatientResponseDto;
import com.vention.medfast.patient.model.Gender;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.user.dto.UserCreateDto;
import com.vention.medfast.user.model.RoleType;
import com.vention.medfast.user.model.Status;
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
		UserCreateDto userDto = new UserCreateDto("Jane", "Smith", "jane@example.com", "pass", LocalDate.of(2000, 1, 1),
				"http://img.com");
		AddressCreateDto addressDto = new AddressCreateDto("Street", "City", "123", "State", "1", "00000", "+123456");

		PatientCreateDto dto = PatientCreateDto.builder()
			.user(userDto)
			.address(addressDto)
			.citizenship("Canada")
			.gender(Gender.FEMALE)
			.build();

		Patient result = patientMapper.toEntity(dto);

		assertThat(result).isNotNull();
		assertThat(result.getCitizenship()).isEqualTo("Canada");
		assertThat(result.getGender()).isEqualTo(Gender.FEMALE);

		if (result.getUser() != null) {
			assertThat(result.getUser().getFirstName()).isEqualTo("Jane");
		}
	}

	@Test
	void shouldMapPatientCreateDtoToUserWhenInputIsValid() {
		UserCreateDto userDto = new UserCreateDto("Jane", "Smith", "jane@example.com", "pass", LocalDate.of(2000, 1, 1),
				"http://img.com");
		AddressCreateDto addressDto = new AddressCreateDto("Street", "City", "123", "State", "1", "00000", "+123456");

		PatientCreateDto dto = PatientCreateDto.builder()
			.user(userDto)
			.address(addressDto)
			.citizenship("Canada")
			.gender(Gender.FEMALE)
			.build();

		User user = patientMapper.toUser(dto);

		assertThat(dto).isNotNull();

		assertThat(user.getFirstName()).isEqualTo("Jane");
		assertThat(user.getLastName()).isEqualTo("Smith");
		assertThat(user.getEmail()).isEqualTo("jane@example.com");
		assertThat(user.getPasswordHash()).isEqualTo("pass");
		assertThat(user.getProfileUrl()).isEqualTo("http://img.com");
		assertThat(user.getBirthDate()).isEqualTo(LocalDate.of(2000, 1, 1));
		assertThat(user.getRole()).isEqualTo(RoleType.PATIENT);
		assertThat(user.getStatus()).isEqualTo(Status.ACTIVE);
	}

	@Test
	void shouldMapAddressCreateDtoToAddressWhenInputIsValid() {
		AddressCreateDto addressDto = new AddressCreateDto("Street", "City", "123", "State", "1", "00000", "+123456");

		Address address = patientMapper.toAddress(addressDto);

		assertThat(address).isNotNull();
		assertThat(address.getStreet()).isEqualTo("Street");
		assertThat(address.getCity()).isEqualTo("City");
		assertThat(address.getHouseNumber()).isEqualTo("123");
		assertThat(address.getState()).isEqualTo("State");
		assertThat(address.getApartment()).isEqualTo("1");
		assertThat(address.getZipCode()).isEqualTo("00000");
		assertThat(address.getPhone()).isEqualTo("+123456");
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
