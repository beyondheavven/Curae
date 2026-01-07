package com.vention.medfast.service.controller;

import com.jayway.jsonpath.JsonPath;
import com.vention.medfast.AbstractIntegrationTest;
import com.vention.medfast.auth.dto.AuthRequestDto;
import com.vention.medfast.constants.ApiPaths;
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
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProvidedServiceControllerTest extends AbstractIntegrationTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	private String token;

	private final List<Integer> specializationIds = List.of(1);

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
	void shouldCreateServiceWhenInputIsValid() throws Exception {
		ProvidedServiceCreateDto dto = generateDto("Basic Checkup");
		String json = objectMapper.writeValueAsString(dto);

		mockMvc
			.perform(post(ApiPaths.SERVICE_ENDPOINT).header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$").isNumber());
	}

	@Test
	void shouldFindServiceByName() throws Exception {
		String uniqueName = "MRI Scan " + UUID.randomUUID().toString().substring(0, 5);
		createServiceInDb(uniqueName);

		mockMvc
			.perform(get(ApiPaths.SERVICE_ENDPOINT + "/search").header("Authorization", "Bearer " + token)
				.param("query", uniqueName)
				.param("page", "0")
				.param("size", "10"))

			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content", Matchers.hasSize(1)))
			.andExpect(jsonPath("$.data.content[0].name").value(uniqueName))
			.andExpect(jsonPath("$.data.totalElements").value(1));
	}

	@Test
	void shouldReturnEmptySearchWhenNoMatch() throws Exception {
		mockMvc
			.perform(get(ApiPaths.SERVICE_ENDPOINT + "/search").header("Authorization", "Bearer " + token)
				.param("query", "NonExistentServiceXYZ"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content").isEmpty());
	}

	@Test
	void shouldGetById() throws Exception {
		Long id = createServiceInDb("X-Ray");

		mockMvc.perform(get(ApiPaths.SERVICE_ENDPOINT + "/{id}", id).header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("X-Ray"));
	}

	@Test
	void shouldUpdateService() throws Exception {
		Long id = createServiceInDb("Old Name");

		ProvidedServiceCreateDto updateDto = new ProvidedServiceCreateDto("New Name", "Updated Description", 200.00, 45,
				specializationIds);
		String json = objectMapper.writeValueAsString(updateDto);

		mockMvc
			.perform(put(ApiPaths.SERVICE_ENDPOINT + "/{id}", id).header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
			.andExpect(status().isOk());

		mockMvc.perform(get(ApiPaths.SERVICE_ENDPOINT + "/{id}", id).header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("New Name"))
			.andExpect(jsonPath("$.price").value(200.00));
	}

	@Test
	void shouldDeleteService() throws Exception {
		Long id = createServiceInDb("To Be Deleted");

		mockMvc.perform(delete(ApiPaths.SERVICE_ENDPOINT + "/{id}", id).header("Authorization", "Bearer " + token))
			.andExpect(status().isNoContent());

		mockMvc.perform(get(ApiPaths.SERVICE_ENDPOINT + "/{id}", id).header("Authorization", "Bearer " + token))
			.andExpect(status().isNotFound());
	}

	private Long createServiceInDb(String name) throws Exception {
		ProvidedServiceCreateDto dto = generateDto(name);
		String json = objectMapper.writeValueAsString(dto);

		String response = mockMvc
			.perform(post(ApiPaths.SERVICE_ENDPOINT).header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString();

		return Long.parseLong(response);
	}

	private ProvidedServiceCreateDto generateDto(String name) {
		return new ProvidedServiceCreateDto(name, "Description for " + name, 100.00, 30, specializationIds);
	}

}
