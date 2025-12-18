package com.vention.medfast.patient.mapper;

import com.vention.medfast.address.model.Address;
import com.vention.medfast.patient.model.Gender;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.registration.dto.PersonDto;
import com.vention.medfast.user.model.User;
import java.time.LocalDate;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class PatientMapperTest {

	private final PatientMapper mapper = Mappers.getMapper(PatientMapper.class);

	@Test
	void shouldMapPersonDtoToPatientEntityWhenDependenciesProvided() {
		PersonDto dto = new PersonDto("John", "Doe", LocalDate.of(1990, 1, 1), Gender.MALE, "secret123",
				"john.doe@mail.com", "+123456789", "USA");

		User user = new User();
		user.setEmail(dto.getEmail());
		user.setFirstName(dto.getName());
		user.setLastName(dto.getLastName());

		Address address = new Address();
		address.setCityName("Metropolis");
		address.setStreetName("Main Street");
		address.setHouseNumber("42");
		address.setApartmentNumber("12B");
		address.setStateName("State");
		address.setZipCode("12345");
		address.setPhone(dto.getPhoneNumber());

		Patient patient = mapper.toEntity(dto, user, address);

		assertThat(patient).isNotNull();
		assertThat(patient.getId()).isNull();
		assertThat(patient.getDateOfBirth()).isEqualTo(dto.getBirthDate());
		assertThat(patient.getGender()).isEqualTo(dto.getGender());
		assertThat(patient.getCitizenship()).isEqualTo(dto.getCitizenship());
		assertThat(patient.getUser()).isSameAs(user);
		assertThat(patient.getAddress()).isSameAs(address);
	}

	@Test
	void shouldReturnNullWhenAllArgumentsNull() {
		assertThat(mapper.toEntity(null, null, null)).isNull();
	}

}
