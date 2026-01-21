package com.vention.medfast.country.controller;

import com.vention.medfast.AbstractControllerIntegrationTest;
import com.vention.medfast.constants.ApiPaths;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CountryControllerTest extends AbstractControllerIntegrationTest {

	@Test
	void shouldReturnCountriesListWithDefaultPaginationWhenCountriesPresent() throws Exception {
		performRequest(HttpMethod.GET, ApiPaths.COUNTRY_ENDPOINT, null).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(10))))
			.andExpect(jsonPath("$.data.content[*].isoCode", hasItems("US", "CA", "DE", "FR", "UA", "BY")))
			.andExpect(jsonPath("$.data.content[?(@.isoCode == 'US')].name").value("United States"))
			.andExpect(jsonPath("$.data.content[?(@.isoCode == 'US')].citizenship").value("American"))
			.andExpect(jsonPath("$.data.content[?(@.isoCode == 'US')].zipExample").value("90210"))
			.andExpect(jsonPath("$.data.content[?(@.isoCode == 'CA')].name").value("Canada"))
			.andExpect(jsonPath("$.data.content[?(@.isoCode == 'CA')].citizenship").value("Canadian"))
			.andExpect(jsonPath("$.data.content[?(@.isoCode == 'CA')].zipExample").value("K1A 0B1"))
			.andExpect(jsonPath("$.data.content[?(@.isoCode == 'NL')].name").value("Netherlands"))
			.andExpect(jsonPath("$.data.content[?(@.isoCode == 'NL')].citizenship").value("Dutch"))
			.andExpect(jsonPath("$.data.content[?(@.isoCode == 'NL')].zipExample").value("1011 AC"))
			.andExpect(jsonPath("$.data.content[?(@.isoCode == 'UA')].name").value("Ukraine"))
			.andExpect(jsonPath("$.data.content[?(@.isoCode == 'UA')].citizenship").value("Ukrainian"))
			.andExpect(jsonPath("$.data.totalElements", greaterThanOrEqualTo(12)))
			.andExpect(jsonPath("$.data.totalPages", greaterThanOrEqualTo(1)))
			.andExpect(jsonPath("$.data.first").value(true));
	}

}
