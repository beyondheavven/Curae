package com.vention.medfast.statistics.controller;

import com.vention.medfast.AbstractControllerIntegrationTest;
import com.vention.medfast.constants.ApiPaths;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StatisticsControllerTest extends AbstractControllerIntegrationTest {

	@Test
	void shouldReturnStatisticsWhenInputIsValid() throws Exception {
		LocalDate startDate = LocalDate.of(2026, 1, 1);
		LocalDate endDate = LocalDate.of(2026, 1, 24);

		String url = String.format("%s%s?startDate=%s&endDate=%s", ApiPaths.STATISTICS_ENDPOINT,
				ApiPaths.ADMIN_ENDPOINT, startDate, endDate);

		performRequest(HttpMethod.GET, url, null).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.period.startDate").value(startDate.toString()))
			.andExpect(jsonPath("$.data.period.endDate").value(endDate.toString()))

			.andExpect(jsonPath("$.data.appointments.stats.total").value(2))
			.andExpect(jsonPath("$.data.appointments.stats.scheduled").value(0))
			.andExpect(jsonPath("$.data.appointments.stats.completed").value(2))

			.andExpect(jsonPath("$.data.visits.stats.total").value(1))
			.andExpect(jsonPath("$.data.visits.stats.completed").value(1))

			.andExpect(jsonPath("$.data.labResults.stats.total").value(1))
			.andExpect(jsonPath("$.data.labResults.results.processing").value(1))
			.andExpect(jsonPath("$.data.labResults.results.ready").value(3))
			.andExpect(jsonPath("$.data.labResults.results.failed").value(0));
	}

	@Test
	void shouldReturnBadRequestWhenDateRangeIsInvalid() throws Exception {
		LocalDate startDate = LocalDate.of(2026, 1, 12);
		LocalDate endDate = LocalDate.of(2026, 1, 1);

		String url = String.format("%s%s?startDate=%s&endDate=%s", ApiPaths.STATISTICS_ENDPOINT,
				ApiPaths.ADMIN_ENDPOINT, startDate, endDate);

		performRequest(HttpMethod.GET, url, null).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.errorCode").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.error.message").value("Validation failed"))
			.andExpect(jsonPath("$.error.validationErrors[0].field").value("statisticsPeriodRequestDto"))
			.andExpect(jsonPath("$.error.validationErrors[0].message").value("End date must be before start date"));
	}

	@Test
	void shouldReturnBadRequestWhenParameterIsMissing() throws Exception {
		LocalDate startDate = LocalDate.of(2026, 1, 1);

		String url = String.format("%s%s?startDate=%s", ApiPaths.STATISTICS_ENDPOINT, ApiPaths.ADMIN_ENDPOINT,
				startDate);

		performRequest(HttpMethod.GET, url, null).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.errorCode").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.error.validationErrors[0].field").value("endDate"))
			.andExpect(jsonPath("$.error.validationErrors[0].message").value("End date is required"));
	}

	@Test
	void shouldReturnBadRequestWhenBothParametersMissing() throws Exception {
		String url = ApiPaths.STATISTICS_ENDPOINT + ApiPaths.ADMIN_ENDPOINT;

		performRequest(HttpMethod.GET, url, null).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.errorCode").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.error.validationErrors", hasSize(2)))
			.andExpect(jsonPath("$.error.validationErrors[*].field", containsInAnyOrder("startDate", "endDate")));
	}

	@Test
	void shouldReturnBadRequestWhenDateIsInFuture() throws Exception {
		LocalDate futureStartDate = LocalDate.now().plusYears(1);
		LocalDate endDate = futureStartDate.plusDays(5);

		String url = String.format("%s%s?startDate=%s&endDate=%s", ApiPaths.STATISTICS_ENDPOINT,
				ApiPaths.ADMIN_ENDPOINT, futureStartDate, endDate);

		performRequest(HttpMethod.GET, url, null).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.validationErrors[0].field").value("startDate"))
			.andExpect(jsonPath("$.error.validationErrors[0].rejectedValue").value(futureStartDate.toString()))
			.andExpect(jsonPath("$.error.validationErrors[0].message").value("Start date cannot be in the future"));
	}

}
