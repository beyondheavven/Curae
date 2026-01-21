package com.vention.medfast.patient.controller;

import com.vention.medfast.AbstractControllerIntegrationTest;
import com.vention.medfast.address.dto.AddressCreateDto;
import com.vention.medfast.address.model.Address;
import com.vention.medfast.patient.dto.PatientCreateDto;
import com.vention.medfast.patient.model.Gender;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.user.dto.UserCreateDto;
import com.vention.medfast.user.model.RoleType;
import com.vention.medfast.user.model.Status;
import com.vention.medfast.user.model.User;
import java.time.LocalDate;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PatientControllerTest extends AbstractControllerIntegrationTest {

	@Test
	void shouldCreatePatientWhenInputIsValid() throws Exception {
		PatientCreateDto createDto = generateValidPatientDto();

		performRequest(HttpMethod.POST, "/patients", createDto).andExpect(status().isCreated())
			.andExpect(jsonPath("$.data.id").isNumber())
			.andExpect(jsonPath("$.data.firstName").value(createDto.user().firstName()))
			.andExpect(jsonPath("$.data.lastName").value(createDto.user().lastName()))
			.andExpect(jsonPath("$.data").exists());
	}

	@Test
	void shouldReturnPatientByIdWhenInputIsValid() throws Exception {
		long createdPatientId = createValidPatient();

		performRequest(HttpMethod.GET, "/patients/{id}", null, createdPatientId).andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(createdPatientId))
			.andExpect(jsonPath("$.data.firstName").isNotEmpty())
			.andExpect(jsonPath("$.data.lastName").isNotEmpty());
	}

	@Test
	void shouldReturn404WhenPatientNotFound() throws Exception {
		performRequest(HttpMethod.GET, "/patients/{id}", null, 999).andExpect(status().isNotFound())
			.andExpect(jsonPath("$.error.message").value("Patient not found"));
	}

	@Test
	void shouldReturn400WhenValidationErrorsPresentWithCreate() throws Exception {
		UserCreateDto invalidUser = new UserCreateDto("", "", "", "invalid-email", LocalDate.now().plusDays(1), "");

		AddressCreateDto address = new AddressCreateDto("Street", "City", "1", "State", "10", "12345", "1234567890");

		PatientCreateDto invalidDto = PatientCreateDto.builder()
			.user(invalidUser)
			.address(address)
			.citizenship("")
			.gender(Gender.MALE)
			.build();

		performRequest(HttpMethod.POST, "/patients", invalidDto).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.message").value("Validation failed"))
			.andExpect(jsonPath("$.error.validationErrors").isArray())
			.andExpect(jsonPath("$.error.validationErrors", Matchers.not(Matchers.empty())))
			.andExpect(jsonPath("$.error.validationErrors[*].message", Matchers.hasItem(Matchers.notNullValue())));
	}

	@Test
	void shouldReturnErrorWhenEmailAlreadyExists() throws Exception {
		PatientCreateDto originalDto = generateValidPatientDto();

		performRequest(HttpMethod.POST, "/patients", originalDto);

		performRequest(HttpMethod.POST, "/patients", originalDto).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.message").value("User with this email already exists"));
	}

	private PatientCreateDto generateValidPatientDto() {
		String uniqueEmail = "test" + UUID.randomUUID() + "@med.com";

		UserCreateDto userDto = new UserCreateDto("John", "Doe", uniqueEmail, "securePassword123",
				LocalDate.of(2000, 1, 1), "");

		AddressCreateDto addressDto = new AddressCreateDto("Baker Street", "London", "221B", "London", "1", "NW1 6XE",
				"+44123456789");

		return PatientCreateDto.builder()
			.user(userDto)
			.address(addressDto)
			.citizenship("USA")
			.gender(Gender.MALE)
			.build();
	}

	private long createValidPatient() {
		String uniqueEmail = "test" + UUID.randomUUID() + "@med.com";

		User user = new User();
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setEmail(uniqueEmail);
		user.setPasswordHash("secret");
		user.setBirthDate(LocalDate.of(2000, 1, 1));
		user.setRole(RoleType.PATIENT);
		user.setStatus(Status.ACTIVE);
		em.persist(user);

		Address address = new Address();
		address.setStreet("Baker Street");
		address.setCity("London");
		address.setHouseNumber("221B");
		address.setState("London");
		address.setApartment("1");
		address.setZipCode("NW1 6XE");
		em.persist(address);

		Patient patient = new Patient();
		patient.setUser(user);
		patient.setAddress(address);
		patient.setCitizenship("USA");
		patient.setGender(Gender.MALE);
		em.persist(patient);

		em.flush();
		em.clear();

		return patient.getId();
	}

}
