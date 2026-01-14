package com.vention.medfast.service.controller;

import com.vention.medfast.AbstractControllerIntegrationTest;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.service.dto.ProvidedServiceCreateDto;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.specialization.model.Specialization;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProvidedServiceControllerTest extends AbstractControllerIntegrationTest {

	private final List<Integer> specializationIds = List.of(1);

	@Test
	void shouldCreateServiceWhenInputIsValid() throws Exception {
		ProvidedServiceCreateDto dto = new ProvidedServiceCreateDto("Basic Checkup", "Description for Basic Checkup",
				100.00, 30, specializationIds);

		performRequest(HttpMethod.POST, ApiPaths.SERVICE_ENDPOINT, dto).andExpect(status().isCreated())
			.andExpect(jsonPath("$.data").isNumber());
	}

	@Test
	void shouldReturnAllServicesWhenTheyExist() throws Exception {
		performRequest(HttpMethod.GET, "/services", null)

			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(3)))
			.andExpect(jsonPath("$[0].name").value("General Checkup"))
			.andExpect(jsonPath("$[1].name").value("Complex Diagnosis"))
			.andExpect(jsonPath("$[2].name").value("Quick Consultation"));
	}

	@Test
	void shouldFindServiceByName() throws Exception {
		String uniqueName = "General Checkup";

		performRequest(HttpMethod.GET, ApiPaths.SERVICE_ENDPOINT + "/search?query={q}&page=0&size=10", null, uniqueName)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content", hasSize(1)))
			.andExpect(jsonPath("$.data.content[0].name").value(uniqueName))
			.andExpect(jsonPath("$.data.totalElements").value(1));
	}

	@Test
	void shouldReturnEmptySearchWhenNoMatch() throws Exception {
		String nonExistingName = "NonExistentServiceXYZ";

		performRequest(HttpMethod.GET, ApiPaths.SERVICE_ENDPOINT + "/search?query={q}", null, nonExistingName)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content").isEmpty());
	}

	@Test
	void shouldGetById() throws Exception {
		performRequest(HttpMethod.GET, ApiPaths.SERVICE_ENDPOINT + "/{id}", null, 1).andExpect(status().isOk())
			.andExpect(jsonPath("$.data.name").value("General Checkup"));
	}

	@Test
	void shouldUpdateService() throws Exception {
		Long id = createServiceInDb("Old Name");

		ProvidedServiceCreateDto updateDto = new ProvidedServiceCreateDto("New Name", "Updated Description", 200.00, 45,
				specializationIds);

		performRequest(HttpMethod.PUT, ApiPaths.SERVICE_ENDPOINT + "/{id}", updateDto, id).andExpect(status().isOk());

		ProvidedService service = em.find(ProvidedService.class, id);

		assertThat(service).isNotNull();
		assertThat(service.getName()).isEqualTo("New Name");
		assertThat(service.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(200.00));
	}

	@Test
	void shouldDeleteService() throws Exception {
		Long id = createServiceInDb("To Be Deleted");

		performRequest(HttpMethod.DELETE, ApiPaths.SERVICE_ENDPOINT + "/{id}", null, id)
			.andExpect(status().isNoContent());

		ProvidedService service = em.find(ProvidedService.class, id);

		assertThat(service).isNull();
	}

	private Long createServiceInDb(String name) {
		ProvidedService service = new ProvidedService();
		service.setName(name);
		service.setDescription("Description for " + name);
		service.setPrice(BigDecimal.valueOf(100.00));
		service.setDuration(30);

		List<Specialization> specs = specializationIds.stream()
			.map(id -> em.getReference(Specialization.class, id))
			.toList();
		service.setSpecializations(specs);

		em.persist(service);
		em.flush();
		em.clear();

		return service.getId();
	}

}
