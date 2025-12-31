package com.vention.medfast.patient.controller;

import com.jayway.jsonpath.JsonPath;
import com.vention.medfast.AbstractIntegrationTest;
import com.vention.medfast.address.dto.AddressCreateDto;
import com.vention.medfast.auth.dto.AuthRequestDto;
import com.vention.medfast.patient.dto.PatientCreateDto;
import com.vention.medfast.patient.model.Gender;
import com.vention.medfast.user.dto.UserCreateDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PatientControllerTest extends AbstractIntegrationTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	private String token;

	@BeforeAll
	void obtainToken() throws Exception {
		AuthRequestDto loginRequest = new AuthRequestDto("admin@med.com", "pass");
		String json = objectMapper.writeValueAsString(loginRequest);

		String response = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		token = JsonPath.read(response, "$.data.accessToken");
	}

	@Test
	void shouldCreatePatientWhenInputIsValid() throws Exception {
		PatientCreateDto createDto = generateValidPatientDto();
		String json = objectMapper.writeValueAsString(createDto);

		mockMvc
			.perform(post("/patients").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.data.id").isNumber())
			.andExpect(jsonPath("$.data.firstName").value(createDto.user().firstName()))
			.andExpect(jsonPath("$.data.lastName").value(createDto.user().lastName()))
			.andExpect(jsonPath("$.data").exists());
	}

	@Test
	void shouldReturnPatientByIdWhenInputIsValid() throws Exception {
		long createdPatientId = createValidPatient();

		mockMvc.perform(get("/patients/{id}", createdPatientId).header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(createdPatientId))
			.andExpect(jsonPath("$.data.firstName").isNotEmpty())
			.andExpect(jsonPath("$.data.lastName").isNotEmpty());
	}

	@Test
	void shouldReturn404WhenPatientNotFound() throws Exception {
		long notExistingId = 99999L;

		mockMvc.perform(get("/patients/{id}", notExistingId).header("Authorization", "Bearer " + token))
			.andExpect(status().isNotFound())
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

		String json = objectMapper.writeValueAsString(invalidDto);

		mockMvc
			.perform(post("/patients").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.errorCode").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.error.message", Matchers.notNullValue()));
	}

	@Test
	void shouldReturnErrorWhenEmailAlreadyExists() throws Exception {
		PatientCreateDto originalDto = generateValidPatientDto();
		String json = objectMapper.writeValueAsString(originalDto);

		mockMvc
			.perform(post("/patients").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
			.andExpect(status().isCreated());

		mockMvc
			.perform(post("/patients").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.message").value("User with this email already exists"));
	}

	private long createValidPatient() throws Exception {
		PatientCreateDto createDto = generateValidPatientDto();
		String createJson = objectMapper.writeValueAsString(createDto);

		String createResponse = mockMvc
			.perform(post("/patients").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(createJson))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString();

		return ((Number) JsonPath.read(createResponse, "$.data.id")).longValue();
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

}
