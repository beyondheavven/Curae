package com.vention.medfast.service.controller;

import com.jayway.jsonpath.JsonPath;
import com.vention.medfast.AbstractIntegrationTest;
import com.vention.medfast.auth.dto.AuthRequestDto;
import com.vention.medfast.service.dto.ProvidedServiceCreateDto;
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
public class ProvidedServiceControllerTest extends AbstractIntegrationTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	String token;

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
	public void shouldReturnServiceByIdWhenInputIsValid() throws Exception {
		mockMvc.perform(get("/services/{id}", 1).header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.name").value("General Checkup"))
			.andExpect(jsonPath("$.description").value("Routine physical examination"))
			.andExpect(jsonPath("$.price").value(150.0))
			.andExpect(jsonPath("$.duration").value(30))

			.andExpect(jsonPath("$.specializations").isArray())
			.andExpect(jsonPath("$.specializations.length()").value(1))

			.andExpect(jsonPath("$.specializations[0].id").value(1))
			.andExpect(jsonPath("$.specializations[0].name").value("General Practitioner"))
			.andExpect(jsonPath("$.specializations[0].description")
				.value("Primary care provider dealing with a wide range of diseases"));
	}

	@Test
	void shouldReturn404WhenSpecializationNotFound() throws Exception {
		long notExistingId = 999L;

		mockMvc.perform(get("/services/{id}", notExistingId).header("Authorization", "Bearer " + token))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Service not found"))
			.andExpect(jsonPath("$.statusCode").value(404));
	}

	@Test
	public void shouldReturnServiceListWhenServicesPresent() throws Exception {
		mockMvc.perform(get("/services").header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())

			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$.length()").value(3))

			.andExpect(jsonPath("$[0].id").value(1))
			.andExpect(jsonPath("$[0].name").value("General Checkup"))
			.andExpect(jsonPath("$[0].description").value("Routine physical examination"))
			.andExpect(jsonPath("$[0].price").value(150.0))
			.andExpect(jsonPath("$[0].duration").value(30))
			.andExpect(jsonPath("$[0].specializations").isArray())
			.andExpect(jsonPath("$[0].specializations.length()").value(1))
			.andExpect(jsonPath("$[0].specializations[0].id").value(1))

			.andExpect(jsonPath("$[1].id").value(2))
			.andExpect(jsonPath("$[1].name").value("Complex Diagnosis"))
			.andExpect(jsonPath("$[1].description").value("In-depth analysis of symptoms"))
			.andExpect(jsonPath("$[1].price").value(500.0))
			.andExpect(jsonPath("$[1].duration").value(60))
			.andExpect(jsonPath("$[1].specializations").isArray())
			.andExpect(jsonPath("$[1].specializations.length()").value(2))
			.andExpect(jsonPath("$[1].specializations[0].id").value(2))
			.andExpect(jsonPath("$[1].specializations[1].id").value(3))

			.andExpect(jsonPath("$[2].id").value(3))
			.andExpect(jsonPath("$[2].name").value("Quick Consultation"))
			.andExpect(jsonPath("$[2].description").value("Brief medical advice"))
			.andExpect(jsonPath("$[2].price").value(75.0))
			.andExpect(jsonPath("$[2].duration").value(15))
			.andExpect(jsonPath("$[2].specializations").isArray())
			.andExpect(jsonPath("$[2].specializations.length()").value(1))
			.andExpect(jsonPath("$[2].specializations[0].id").value(1));
	}

	@Test
	void shouldCreateServiceWhenInputIsValid() throws Exception {
		int createdServiceId = createValidService();

		mockMvc.perform(get("/services/{id}", createdServiceId).header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("Test Service"))
			.andExpect(jsonPath("$.description").value("Test service description"))
			.andExpect(jsonPath("$.price").value(200.0))
			.andExpect(jsonPath("$.duration").value(45))
			.andExpect(jsonPath("$.specializations[*].id", Matchers.hasItem(1)));

		mockMvc.perform(get("/specializations/{id}", 1).header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.services[*].id", Matchers.hasItem(createdServiceId)));

		mockMvc.perform(delete("/services/{id}", createdServiceId).header("Authorization", "Bearer " + token))
			.andExpect(status().isNoContent());
	}

	@Test
	void shouldReturn400WhenValidationErrorsPresentWithCreateService() throws Exception {
		ProvidedServiceCreateDto dto = new ProvidedServiceCreateDto("A", "", -10.0, 0, List.of());

		String json = objectMapper.writeValueAsString(dto);

		mockMvc
			.perform(post("/services").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Validation error"))
			.andExpect(jsonPath("$.statusCode").value(400))
			.andExpect(jsonPath("$.fieldErrors.name").value("Name should be between 5 and 255 characters"))
			.andExpect(
					jsonPath("$.fieldErrors.description").value("Description should be between 10 and 255 characters"))
			.andExpect(jsonPath("$.fieldErrors.price").value("Price must be greater than 0"))
			.andExpect(jsonPath("$.fieldErrors.duration").value("Duration must be at least 1 minute"))
			.andExpect(jsonPath("$.fieldErrors.specializationsIds").value("At least one specialization is required"));
	}

	@Test
	void shouldDeleteServiceByIdWhenInputIsValid() throws Exception {
		int createdServiceId = createValidService();

		mockMvc.perform(delete("/services/{id}", createdServiceId).header("Authorization", "Bearer " + token))
			.andExpect(status().isNoContent());

		mockMvc.perform(get("/services/{id}", createdServiceId).header("Authorization", "Bearer " + token))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Service not found"))
			.andExpect(jsonPath("$.statusCode").value(404));
	}

	@Test
	void shouldReturn404WhenServiceForDeleteNotFound() throws Exception {
		long notExistingId = 999L;

		mockMvc.perform(delete("/services/{id}", notExistingId).header("Authorization", "Bearer " + token))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Service not found"))
			.andExpect(jsonPath("$.statusCode").value(404));
	}

	@Test
	void shouldUpdateServiceWhenInputIsValid() throws Exception {
		int serviceId = createValidService();

		ProvidedServiceCreateDto updateDto = new ProvidedServiceCreateDto("Updated Service", "Updated description",
				200.0, 45, List.of(2));

		String updateJson = objectMapper.writeValueAsString(updateDto);

		mockMvc
			.perform(put("/services/{id}", serviceId).header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateJson))
			.andExpect(status().isOk());

		mockMvc.perform(get("/services/{id}", serviceId).header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("Updated Service"))
			.andExpect(jsonPath("$.description").value("Updated description"))
			.andExpect(jsonPath("$.specializations[*].id", Matchers.containsInAnyOrder(2)));

		mockMvc.perform(get("/specializations/{id}", 1).header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.services[*].id", Matchers.not(Matchers.hasItem(serviceId))));

		mockMvc.perform(get("/specializations/{id}", 3).header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.services[*].id", Matchers.not(Matchers.hasItem(serviceId))));

		mockMvc.perform(get("/specializations/{id}", 2).header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.services[*].id", Matchers.hasItem(serviceId)));

		mockMvc.perform(delete("/services/{id}", serviceId).header("Authorization", "Bearer " + token))
			.andExpect(status().isNoContent());
	}

	@Test
	void shouldReturn404WhenServiceForUpdateNotFound() throws Exception {
		long notExistingId = 999L;
		ProvidedServiceCreateDto updateDto = new ProvidedServiceCreateDto("Updated Service", "Updated description",
				200.0, 45, List.of(2));

		String updateJson = objectMapper.writeValueAsString(updateDto);

		mockMvc
			.perform(put("/services/{id}", notExistingId).header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateJson))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Service not found"))
			.andExpect(jsonPath("$.statusCode").value(404));
	}

	@Test
	void shouldReturn400WhenValidationErrorsPresentWithUpdateService() throws Exception {
		int serviceId = createValidService();

		ProvidedServiceCreateDto updateDto = new ProvidedServiceCreateDto("A", "", -10.0, 0, List.of());

		String updateJson = objectMapper.writeValueAsString(updateDto);

		mockMvc
			.perform(put("/services/{id}", serviceId).header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateJson))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Validation error"))
			.andExpect(jsonPath("$.statusCode").value(400))
			.andExpect(jsonPath("$.fieldErrors.name").value("Name should be between 5 and 255 characters"))
			.andExpect(
					jsonPath("$.fieldErrors.description").value("Description should be between 10 and 255 characters"))
			.andExpect(jsonPath("$.fieldErrors.price").value("Price must be greater than 0"))
			.andExpect(jsonPath("$.fieldErrors.duration").value("Duration must be at least 1 minute"))
			.andExpect(jsonPath("$.fieldErrors.specializationsIds").value("At least one specialization is required"));

		mockMvc.perform(delete("/services/{id}", serviceId).header("Authorization", "Bearer " + token))
			.andExpect(status().isNoContent());
	}

	private int createValidService() throws Exception {
		ProvidedServiceCreateDto dto = new ProvidedServiceCreateDto("Test Service", "Test service description", 200.0,
				45, List.of(1));

		String json = objectMapper.writeValueAsString(dto);

		String response = mockMvc
			.perform(post("/services").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString();

		return JsonPath.read(response, "$");
	}

}
