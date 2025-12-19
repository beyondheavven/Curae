package com.vention.medfast.patient.controller;

import tools.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.vention.medfast.AbstractIntegrationTest;
import com.vention.medfast.address.dto.AddressCreateDto;
import com.vention.medfast.auth.dto.AuthRequestDto;
import com.vention.medfast.patient.dto.PatientCreateDto;
import com.vention.medfast.patient.model.GenderType;
import com.vention.medfast.user.dto.UserCreateDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PatientControllerTest extends AbstractIntegrationTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	static String token;

	@BeforeAll
	void obtainToken() throws Exception {
		AuthRequestDto loginRequest = new AuthRequestDto("admin@med.com", "pass");

		String json = objectMapper.writeValueAsString(loginRequest);

		String response = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		token = JsonPath.read(response, "$.accessToken");
	}

	@Test
	public void shouldReturnPatientByIdWhenInputIsValid() throws Exception {
		int createdPatientId = createValidPatient();

		mockMvc.perform(get("/patients/{id}", createdPatientId).header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", is(createdPatientId)))
			.andExpect(jsonPath("$.firstName", is("John")))
			.andExpect(jsonPath("$.lastName", is("Doe")));
	}

	@Test
	void shouldReturn404WhenPatientNotFound() throws Exception {
		long notExistingId = 99999L;

		mockMvc.perform(get("/patients/{id}", notExistingId).header("Authorization", "Bearer " + token))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Patient not found"))
			.andExpect(jsonPath("$.statusCode").value(404));
	}

	@Test
	void shouldCreatePatientWhenInputIsValid() throws Exception {
		String uniqueEmail = "jane.doe." + UUID.randomUUID() + "@example.com";
		String profileUrl = "http://profile.url";
		UserCreateDto userDto = new UserCreateDto("Jane", "Doe", uniqueEmail, "password123", profileUrl);
		AddressCreateDto addressDto = new AddressCreateDto("Baker St", "London", "221B", "UK", "1", "NW1 6XE",
				"+4412345678");
		PatientCreateDto requestDto = new PatientCreateDto(userDto, addressDto, "USA", GenderType.FEMALE,
				LocalDate.of(1995, 5, 20));

		String json = objectMapper.writeValueAsString(requestDto);

		String createResponse = mockMvc
			.perform(post("/patients").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.firstName").value("Jane"))
			.andExpect(jsonPath("$.lastName").value("Doe"))
			.andExpect(jsonPath("$.profileUrl").value(profileUrl))
			.andExpect(jsonPath("$.id").exists())
			.andReturn()
			.getResponse()
			.getContentAsString();

		int createdId = JsonPath.read(createResponse, "$.id");

		mockMvc.perform(get("/patients/{id}", createdId).header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(createdId));
	}

	@Test
	void shouldReturn400WhenValidationErrorsPresentWithCreate() throws Exception {
		PatientCreateDto invalidDto = new PatientCreateDto(null, null, "", null, LocalDate.now().plusDays(1));

		String json = objectMapper.writeValueAsString(invalidDto);

		mockMvc
			.perform(post("/patients").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Validation error"))
			.andExpect(jsonPath("$.statusCode").value(400))
			.andExpect(jsonPath("$.fieldErrors.user").exists())
			.andExpect(jsonPath("$.fieldErrors.address").exists())
			.andExpect(jsonPath("$.fieldErrors.citizenship").exists())
			.andExpect(jsonPath("$.fieldErrors.birthDate").exists());
	}

	private int createValidPatient() throws Exception {
		String uniqueEmail = "john.doe." + UUID.randomUUID() + "@example.com";

		UserCreateDto userDto = new UserCreateDto("John", "Doe", uniqueEmail, "password123", "http://profile.url");
		AddressCreateDto addressDto = new AddressCreateDto("Baker St", "London", "221B", "UK", "1", "NW1 6XE",
				"+4412345678");

		PatientCreateDto requestDto = new PatientCreateDto(userDto, addressDto, "USA", GenderType.FEMALE,
				LocalDate.of(1990, 1, 1));

		String createResponse = mockMvc
			.perform(post("/patients").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString();

		return JsonPath.read(createResponse, "$.id");
	}

}
