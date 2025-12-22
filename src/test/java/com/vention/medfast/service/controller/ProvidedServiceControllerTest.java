package com.vention.medfast.service.controller;

import com.jayway.jsonpath.JsonPath;
import com.vention.medfast.AbstractIntegrationTest;
import com.vention.medfast.auth.dto.AuthRequestDto;
import com.vention.medfast.service.dto.ProvidedServiceCreateDto;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import tools.jackson.databind.ObjectMapper;

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

		token = JsonPath.read(response, "$.data.accessToken");
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
			.andExpect(jsonPath("$.error.message").value("Service not found"));
	}

	@Test
	public void shouldReturnServiceListWhenServicesPresent() throws Exception {
		mockMvc.perform(get("/services").header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$", Matchers.hasSize(Matchers.greaterThanOrEqualTo(3))))
			.andExpect(jsonPath("$[*].id", Matchers.hasItems(1, 2, 3)))
			.andExpect(jsonPath("$[?(@.id==1)].name").value(Matchers.contains("General Checkup")))
			.andExpect(jsonPath("$[?(@.id==2)].name").value(Matchers.contains("Complex Diagnosis")))
			.andExpect(jsonPath("$[?(@.id==3)].name").value(Matchers.contains("Quick Consultation")));
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
			.andExpect(jsonPath("$.data.services[*].id", Matchers.hasItem(createdServiceId)));

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
			.andExpect(jsonPath("$.error.errorCode").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.error.message",
					Matchers.anyOf(Matchers.containsString("name"), Matchers.containsString("description"),
							Matchers.containsString("price"), Matchers.containsString("duration"),
							Matchers.containsString("specializationsIds"))));
	}

	@Test
	void shouldDeleteServiceByIdWhenInputIsValid() throws Exception {
		int createdServiceId = createValidService();

		mockMvc.perform(delete("/services/{id}", createdServiceId).header("Authorization", "Bearer " + token))
			.andExpect(status().isNoContent());

		mockMvc.perform(get("/services/{id}", createdServiceId).header("Authorization", "Bearer " + token))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.error.message").value("Service not found"));
	}

	@Test
	void shouldReturn404WhenServiceForDeleteNotFound() throws Exception {
		long notExistingId = 999L;

		mockMvc.perform(delete("/services/{id}", notExistingId).header("Authorization", "Bearer " + token))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.error.message").value("Service not found"));
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
			.andExpect(jsonPath("$.data.services[*].id", Matchers.not(Matchers.hasItem(serviceId))));

		mockMvc.perform(get("/specializations/{id}", 3).header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.services[*].id", Matchers.not(Matchers.hasItem(serviceId))));

		mockMvc.perform(get("/specializations/{id}", 2).header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.services[*].id", Matchers.hasItem(serviceId)));

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
			.andExpect(jsonPath("$.error.message").value("Service not found"));
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
			.andExpect(jsonPath("$.error.errorCode").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.error.message",
					Matchers.anyOf(Matchers.containsString("name"), Matchers.containsString("description"),
							Matchers.containsString("price"), Matchers.containsString("duration"),
							Matchers.containsString("specializationsIds"))));

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
