package com.vention.medfast.doctor.controller;

import com.vention.medfast.AbstractControllerIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DoctorControllerTest extends AbstractControllerIntegrationTest {

	@Test
	void shouldSearchDoctorsByQuery() throws Exception {
		String query = "Diagnostician";

		performRequest(HttpMethod.GET, "/doctor?query=" + query, null).andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content", hasSize(1)))
			.andExpect(jsonPath("$.data.content[0].firstName").value("Gregory"))
			.andExpect(jsonPath("$.data.content[0].specialization").value("Diagnostician, General Practitioner"));
	}

	@Test
	void shouldReturnPaginatedResults() throws Exception {
		int size = 2;

		performRequest(HttpMethod.GET, "/doctor?query=&size=" + size + "&page=0", null).andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content", hasSize(2)))
			.andExpect(jsonPath("$.data.totalElements").value(2))
			.andExpect(jsonPath("$.data.totalPages").value(1));
	}

	@Test
	void shouldSortResultsByLastNameAscending() throws Exception {
		performRequest(HttpMethod.GET, "/doctor?query=", null).andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content[0].lastName").value("House"))
			.andExpect(jsonPath("$.data.content[1].lastName").value("Watson"));
	}

}
