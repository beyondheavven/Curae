package com.vention.medfast.specialization.controller;

import com.jayway.jsonpath.JsonPath;
import com.vention.medfast.AbstractIntegrationTest;
import com.vention.medfast.auth.dto.AuthRequestDto;
import com.vention.medfast.specialization.dto.SpecializationCreateDto;
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

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpecializationControllerTest extends AbstractIntegrationTest {

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
	public void shouldReturnSpecializationByIdWhenInputIsValid() throws Exception {
		mockMvc.perform(get("/specializations/{id}", 1).header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").exists())
			.andExpect(jsonPath("$.name").value("General Practitioner"))
			.andExpect(jsonPath("$.description").value("Primary care provider dealing with a wide range of diseases"))

			.andExpect(jsonPath("$.services").isArray())
			.andExpect(jsonPath("$.services.length()").value(2))

			.andExpect(jsonPath("$.services[0].id").value(1))
			.andExpect(jsonPath("$.services[0].name").value("General Checkup"))
			.andExpect(jsonPath("$.services[0].description").value("Routine physical examination"))
			.andExpect(jsonPath("$.services[0].price").value(150.0))
			.andExpect(jsonPath("$.services[0].duration").value(30))

			.andExpect(jsonPath("$.services[1].id").value(3))
			.andExpect(jsonPath("$.services[1].name").value("Quick Consultation"))
			.andExpect(jsonPath("$.services[1].description").value("Brief medical advice"))
			.andExpect(jsonPath("$.services[1].price").value(75.0))
			.andExpect(jsonPath("$.services[1].duration").value(15));
	}

	@Test
	void shouldReturn404WhenSpecializationNotFound() throws Exception {
		long notExistingId = 999L;

		mockMvc.perform(get("/specializations/{id}", notExistingId).header("Authorization", "Bearer " + token))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Specialization not found"))
			.andExpect(jsonPath("$.statusCode").value(404));
	}

	@Test
	public void shouldReturnSpecializationListByWhenSpecializationsPresent() throws Exception {
		mockMvc.perform(get("/specializations").header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())

			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$.length()").value(3))

			.andExpect(jsonPath("$[0].id").value(1))
			.andExpect(jsonPath("$[0].name").value("General Practitioner"))
			.andExpect(
					jsonPath("$[0].description").value("Primary care provider dealing with a wide range of diseases"))
			.andExpect(jsonPath("$[0].services").isArray())
			.andExpect(jsonPath("$[0].services.length()").value(2))

			.andExpect(jsonPath("$[0].services[0].id").value(1))

			.andExpect(jsonPath("$[0].services[1].id").value(3))

			.andExpect(jsonPath("$[1].id").value(2))
			.andExpect(jsonPath("$[1].name").value("Cardiologist"))
			.andExpect(jsonPath("$[1].description").value("Heart and cardiovascular system specialist"))
			.andExpect(jsonPath("$[1].services.length()").value(1))

			.andExpect(jsonPath("$[1].services[0].id").value(2))

			.andExpect(jsonPath("$[2].id").value(3))
			.andExpect(jsonPath("$[2].name").value("Diagnostician"))
			.andExpect(jsonPath("$[2].description").value("Specialist in identifying complex medical conditions"))
			.andExpect(jsonPath("$[2].services.length()").value(1))

			.andExpect(jsonPath("$[2].services[0].id").value(2));
	}

	@Test
	void shouldCreateSpecializationWhenInputIsValid() throws Exception {
		int createdSpecializationId = createValidSpecialization();

		mockMvc
			.perform(get("/specializations/{id}", createdSpecializationId).header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("Test Specialization"))
			.andExpect(jsonPath("$.description").value("Test description"))
			.andExpect(jsonPath("$.services[0].id").value(1))

			.andExpect(jsonPath("$.services[1].id").value(3));

		mockMvc.perform(get("/services/{id}", 1).header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.specializations[*].id", Matchers.hasItem(createdSpecializationId)));

		mockMvc.perform(get("/services/{id}", 3).header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.specializations[*].id", Matchers.hasItem(createdSpecializationId)));

		mockMvc
			.perform(
					delete("/specializations/{id}", createdSpecializationId).header("Authorization", "Bearer " + token))
			.andExpect(status().isNoContent());
	}

	@Test
	void shouldReturn400WhenValidationErrorsPresentWithCreate() throws Exception {
		SpecializationCreateDto dto = new SpecializationCreateDto("A", "", List.of());

		String json = objectMapper.writeValueAsString(dto);

		mockMvc
			.perform(post("/specializations").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Validation error"))
			.andExpect(jsonPath("$.statusCode").value(400))
			.andExpect(jsonPath("$.fieldErrors.name").value("Name should be between 3 and 50 characters"))
			.andExpect(
					jsonPath("$.fieldErrors.description").value("Description should be between 10 and 255 characters"))
			.andExpect(jsonPath("$.fieldErrors.serviceIds").value("At least one service is required"));
	}

	@Test
	void shouldDeleteSpecializationByIdWhenInputIsValid() throws Exception {
		int specializationId = createValidSpecialization();
		mockMvc.perform(delete("/specializations/{id}", specializationId).header("Authorization", "Bearer " + token))
			.andExpect(status().isNoContent());

		mockMvc.perform(get("/specializations/{id}", specializationId).header("Authorization", "Bearer " + token))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Specialization not found"))
			.andExpect(jsonPath("$.statusCode").value(404));
	}

	@Test
	void shouldReturn404WhenSpecializationForDeleteNotFound() throws Exception {
		long notExistingId = 999L;

		mockMvc.perform(delete("/specializations/{id}", notExistingId).header("Authorization", "Bearer " + token))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Specialization not found"))
			.andExpect(jsonPath("$.statusCode").value(404));
	}

	@Test
	void shouldUpdateSpecializationWhenInputIsValid() throws Exception {
		int specializationId = createValidSpecialization();

		SpecializationCreateDto updateDto = new SpecializationCreateDto("Updated Name", "Updated description",
				List.of(2));

		String updateJson = objectMapper.writeValueAsString(updateDto);

		mockMvc
			.perform(put("/specializations/{id}", specializationId).header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateJson))
			.andExpect(status().isOk());

		mockMvc.perform(get("/specializations/{id}", specializationId).header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("Updated Name"))
			.andExpect(jsonPath("$.description").value("Updated description"))
			.andExpect(jsonPath("$.services[*].id", Matchers.containsInAnyOrder(2)));

		mockMvc.perform(get("/services/{id}", 1).header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.specializations[*].id", Matchers.not(Matchers.hasItem(specializationId))));

		mockMvc.perform(get("/services/{id}", 3).header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.specializations[*].id", Matchers.not(Matchers.hasItem(specializationId))));

		mockMvc.perform(get("/services/{id}", 2).header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.specializations[*].id", Matchers.hasItem(specializationId)));

		mockMvc.perform(delete("/specializations/{id}", specializationId).header("Authorization", "Bearer " + token))
			.andExpect(status().isNoContent());
	}

	@Test
	void shouldReturn404WhenSpecializationForUpdateNotFound() throws Exception {
		long notExistingId = 999L;

		SpecializationCreateDto dto = new SpecializationCreateDto("Test Specialization", "Test description",
				List.of(1, 3));

		String json = objectMapper.writeValueAsString(dto);

		mockMvc
			.perform(put("/specializations/{id}", notExistingId).header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Specialization not found"))
			.andExpect(jsonPath("$.statusCode").value(404));
	}

	@Test
	void shouldReturn400WhenValidationErrorsPresentWithUpdate() throws Exception {
		int specializationId = createValidSpecialization();

		SpecializationCreateDto invalidDto = new SpecializationCreateDto("A", "", List.of());

		String invalidJson = objectMapper.writeValueAsString(invalidDto);

		mockMvc
			.perform(put("/specializations/{id}", specializationId).header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(invalidJson))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Validation error"))
			.andExpect(jsonPath("$.statusCode").value(400))
			.andExpect(jsonPath("$.fieldErrors.name").value("Name should be between 3 and 50 characters"))
			.andExpect(
					jsonPath("$.fieldErrors.description").value("Description should be between 10 and 255 characters"))
			.andExpect(jsonPath("$.fieldErrors.serviceIds").value("At least one service is required"));

		mockMvc.perform(delete("/specializations/{id}", specializationId).header("Authorization", "Bearer " + token))
			.andExpect(status().isNoContent());
	}

	private int createValidSpecialization() throws Exception {
		SpecializationCreateDto createDto = new SpecializationCreateDto("Test Specialization", "Test description",
				List.of(1, 3));

		String createJson = objectMapper.writeValueAsString(createDto);
		String createResponse = mockMvc
			.perform(post("/specializations").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(createJson))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString();

		return JsonPath.read(createResponse, "$");
	}

}
