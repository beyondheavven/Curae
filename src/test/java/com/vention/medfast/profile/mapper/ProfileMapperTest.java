package com.vention.medfast.profile.mapper;

import com.vention.medfast.address.model.Address;
import com.vention.medfast.patient.model.Gender;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.profile.dto.request.ContactsUpdateDto;
import com.vention.medfast.profile.dto.request.PersonalDetailsUpdateDto;
import com.vention.medfast.profile.dto.request.ProfileAddressUpdateDto;
import com.vention.medfast.profile.dto.response.ProfileResponseDto;
import com.vention.medfast.user.model.RoleType;
import com.vention.medfast.user.model.Status;
import com.vention.medfast.user.model.User;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ProfileMapperTest {

	private ProfileMapper mapper;

	@BeforeEach
	void setUp() {
		mapper = Mappers.getMapper(ProfileMapper.class);
	}

	@Test
	void shouldMapUserAndPatientToProfileResponse() {
		User user = buildUser();
		Patient patient = buildPatient(user);

		ProfileResponseDto result = mapper.toDto(user, patient);

		assertThat(result).isNotNull();
		assertThat(result.getPersonalDetails().getName()).isEqualTo("John");
		assertThat(result.getPersonalDetails().getLastName()).isEqualTo("Doe");
		assertThat(result.getPersonalDetails().getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
		assertThat(result.getPersonalDetails().getGender()).isEqualTo(Gender.MALE);
		assertThat(result.getPersonalDetails().getCitizenship()).isEqualTo("American");
		assertThat(result.getPersonalDetails().getPhotoUrl()).isEqualTo("photo.jpg");

		assertThat(result.getProfileAddress().getStreet()).isEqualTo("Main");
		assertThat(result.getProfileAddress().getCity()).isEqualTo("New York");
		assertThat(result.getProfileAddress().getHouseNumber()).isEqualTo("10");
		assertThat(result.getProfileAddress().getState()).isEqualTo("NY");
		assertThat(result.getProfileAddress().getApartment()).isEqualTo("1A");
		assertThat(result.getProfileAddress().getZipCode()).isEqualTo("10001");

		assertThat(result.getContactsDto().getEmail()).isEqualTo("john.doe@mail.com");
		assertThat(result.getContactsDto().getPhone()).isEqualTo("+123456789");
		assertThat(result.getStatus()).isEqualTo(Status.ACTIVE);
	}

	@Test
	void shouldUpdateUserAndPatientIgnoringNulls() {
		User user = buildUser();
		Patient patient = buildPatient(user);

		PersonalDetailsUpdateDto dto = new PersonalDetailsUpdateDto("Jane", null, null, null, "Canadian", null);
		mapper.updateUser(dto, user);
		mapper.updatePatient(dto, patient);

		assertThat(user.getFirstName()).isEqualTo("Jane");
		assertThat(user.getLastName()).isEqualTo("Doe");
		assertThat(user.getProfileUrl()).isEqualTo("photo.jpg");

		assertThat(patient.getGender()).isEqualTo(Gender.MALE);
		assertThat(patient.getCitizenship()).isEqualTo("Canadian");
		assertThat(patient.getUser().getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
	}

	@Test
	void shouldUpdateAddressAndContactsIgnoringNulls() {
		Address address = buildAddress();
		ProfileAddressUpdateDto addressDto = new ProfileAddressUpdateDto("Second", null, null, "20", "NY", "20002");
		mapper.updateAddress(addressDto, address);

		assertThat(address.getStreet()).isEqualTo("Second");
		assertThat(address.getCity()).isEqualTo("New York");
		assertThat(address.getApartment()).isEqualTo("1A");
		assertThat(address.getHouseNumber()).isEqualTo("20");
		assertThat(address.getState()).isEqualTo("NY");
		assertThat(address.getZipCode()).isEqualTo("20002");
		assertThat(address.getPhone()).isEqualTo("+123456789");

		ContactsUpdateDto contactsUpdateDto = new ContactsUpdateDto(null, "+987654321");
		mapper.updateAddressFromContacts(contactsUpdateDto, address);

		assertThat(address.getPhone()).isEqualTo("+987654321");
		assertThat(address.getStreet()).isEqualTo("Second");
	}

	private User buildUser() {
		User user = new User();
		user.setId(1L);
		user.setEmail("john.doe@mail.com");
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setPasswordHash("hash");
		user.setProfileUrl("photo.jpg");
		user.setRole(RoleType.PATIENT);
		user.setStatus(Status.ACTIVE);
		return user;
	}

	private Patient buildPatient(User user) {
		Patient patient = new Patient();
		patient.setId(5L);
		patient.setUser(user);
		patient.setAddress(buildAddress());
		patient.getUser().setBirthDate(LocalDate.of(1990, 1, 1));
		patient.setGender(Gender.MALE);
		patient.setCitizenship("American");
		return patient;
	}

	private Address buildAddress() {
		Address address = new Address();
		address.setId(3L);
		address.setStreet("Main");
		address.setCity("New York");
		address.setHouseNumber("10");
		address.setState("NY");
		address.setApartment("1A");
		address.setZipCode("10001");
		address.setPhone("+123456789");
		return address;
	}

}
