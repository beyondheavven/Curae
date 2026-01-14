package com.vention.medfast.specialization.controller;

import com.jayway.jsonpath.JsonPath;
import com.vention.medfast.AbstractControllerIntegrationTest;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.specialization.dto.SpecializationCreateDto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vention.medfast.specialization.model.Specialization;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SpecializationControllerTest extends AbstractControllerIntegrationTest {

	@Test
	void shouldReturnSpecializationByIdWhenInputIsValid() throws Exception {
		performRequest(HttpMethod.GET, "/specializations/{id}", null, 1).andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(1))
			.andExpect(jsonPath("$.data.name").value("General Practitioner"))
			.andExpect(
					jsonPath("$.data.description").value("Primary care provider dealing with a wide range of diseases"))
			.andExpect(jsonPath("$.data.services").isArray())
			.andExpect(jsonPath("$.data.services.length()").value(2))
			.andExpect(jsonPath("$.data.services[0].id").value(1))
			.andExpect(jsonPath("$.data.services[0].name").value("General Checkup"))
			.andExpect(jsonPath("$.data.services[0].description").value("Routine physical examination"))
			.andExpect(jsonPath("$.data.services[0].price").value(150.0))
			.andExpect(jsonPath("$.data.services[0].duration").value(30))
			.andExpect(jsonPath("$.data.services[1].id").value(3))
			.andExpect(jsonPath("$.data.services[1].name").value("Quick Consultation"))
			.andExpect(jsonPath("$.data.services[1].description").value("Brief medical advice"))
			.andExpect(jsonPath("$.data.services[1].price").value(75.0))
			.andExpect(jsonPath("$.data.services[1].duration").value(15));
	}

	@Test
	void shouldReturn404WhenSpecializationNotFound() throws Exception {
		performRequest(HttpMethod.GET, "/specializations/{id}", null, 999).andExpect(status().isNotFound())
			.andExpect(jsonPath("$.error.message").value("Specialization not found"));
	}

	@Test
	void shouldReturnSpecializationListWhenSpecializationsPresent() throws Exception {
		performRequest(HttpMethod.GET, "/specializations", null).andExpect(status().isOk())
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data", Matchers.hasSize(Matchers.greaterThanOrEqualTo(3))))
			.andExpect(jsonPath("$.data[*].id", Matchers.hasItems(1, 2, 3)))
			.andExpect(jsonPath("$.data[?(@.id==1)].name").value(Matchers.contains("General Practitioner")))
			.andExpect(jsonPath("$.data[?(@.id==2)].name").value(Matchers.contains("Cardiologist")))
			.andExpect(jsonPath("$.data[?(@.id==3)].name").value(Matchers.contains("Diagnostician")));
	}

	@Test
	void shouldCreateSpecializationWhenInputIsValid() throws Exception {
		SpecializationCreateDto createDto = new SpecializationCreateDto("Test Specialization", "Test description",
				List.of(1, 3));

		String createResponse = performRequest(HttpMethod.POST, "/specializations", createDto)
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString();

		long createdId = ((Number) JsonPath.read(createResponse, "$.data")).longValue();

		Specialization specialization = em.find(Specialization.class, createdId);

		List<Long> serviceIds = specialization.getServices().stream().map(ProvidedService::getId).toList();

		assertNotNull(specialization);
		assertEquals("Test Specialization", specialization.getName());
		assertEquals("Test description", specialization.getDescription());

		assertEquals(2, serviceIds.size());
		assertTrue(serviceIds.contains(1L));
		assertTrue(serviceIds.contains(3L));
	}

	@Test
	void shouldReturn400WhenValidationErrorsPresentWithCreate() throws Exception {
		SpecializationCreateDto dto = new SpecializationCreateDto("A", "", List.of());

		performRequest(HttpMethod.POST, "/specializations", dto).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.errorCode").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.error.message", Matchers.notNullValue()));
	}

	@Test
	void shouldDeleteSpecializationByIdWhenInputIsValid() throws Exception {
		long specializationId = createValidSpecialization();

		performRequest(HttpMethod.DELETE, "/specializations/{id}", null, specializationId)
			.andExpect(status().isNoContent());

		assertNull(em.find(Specialization.class, specializationId));
	}

	@Test
	void shouldReturn404WhenSpecializationForDeleteNotFound() throws Exception {
		performRequest(HttpMethod.DELETE, "/specializations/{id}", null, 999).andExpect(status().isNotFound())
			.andExpect(jsonPath("$.error.message").value("Specialization not found"));
	}

	@Test
	void shouldUpdateSpecializationWhenInputIsValid() throws Exception {
		long specializationId = createValidSpecialization();

		SpecializationCreateDto updateDto = new SpecializationCreateDto("Updated Name", "Updated description",
				List.of(2));

		performRequest(HttpMethod.PUT, "/specializations/{id}", updateDto, specializationId).andExpect(status().isOk())
			.andExpect(jsonPath("$.data").value(specializationId));

		Specialization specialization = em.find(Specialization.class, specializationId);
		assertNotNull(specialization);
		assertEquals("Updated Name", specialization.getName());
		assertEquals("Updated description", specialization.getDescription());

		Set<Long> serviceIds = specialization.getServices()
			.stream()
			.map(ProvidedService::getId)
			.collect(Collectors.toSet());
		assertEquals(1, serviceIds.size());
		assertTrue(serviceIds.contains(2L));

		ProvidedService service1 = em.find(ProvidedService.class, 1);
		ProvidedService service3 = em.find(ProvidedService.class, 3);
		ProvidedService service2 = em.find(ProvidedService.class, 2);

		assertFalse(service1.getSpecializations().stream().anyMatch(s -> s.getId().equals(specializationId)));
		assertFalse(service3.getSpecializations().stream().anyMatch(s -> s.getId().equals(specializationId)));

		assertTrue(service2.getSpecializations().stream().anyMatch(s -> s.getId().equals(specializationId)));
	}

	@Test
	void shouldReturn404WhenSpecializationForUpdateNotFound() throws Exception {
		SpecializationCreateDto dto = new SpecializationCreateDto("Test Specialization", "Test description",
				List.of(1, 3));

		performRequest(HttpMethod.PUT, "/specializations/{id}", dto, 999).andExpect(status().isNotFound())
			.andExpect(jsonPath("$.error.message").value("Specialization not found"));
	}

	@Test
	void shouldReturn400WhenValidationErrorsPresentWithUpdate() throws Exception {
		SpecializationCreateDto invalidDto = new SpecializationCreateDto("A", "", List.of());

		performRequest(HttpMethod.PUT, "/specializations/{id}", invalidDto, 1).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.errorCode").value("VALIDATION_ERROR"));
	}

	private long createValidSpecialization() {
		Specialization specialization = new Specialization();
		specialization.setName("Test Specialization");
		specialization.setDescription("Test description");

		List<ProvidedService> services = Stream.of(1L, 3L)
			.map(id -> em.getReference(ProvidedService.class, id))
			.toList();

		specialization.setServices(services);

		em.persist(specialization);
		em.flush();
		em.clear();

		return specialization.getId();
	}

}
