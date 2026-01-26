package com.vention.medfast.statistics.controller;

import com.vention.medfast.api.response.ErrorResponseDto;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.statistics.dto.request.StatisticsPeriodRequestDto;
import com.vention.medfast.statistics.dto.response.StatisticsResponseDto;
import com.vention.medfast.statistics.service.StatisticsService;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.STATISTICS_ENDPOINT)
@Tag(name = "Statistics", description = "Admin endpoints for platform analytics and reporting.")
public class StatisticsController {

	private final StatisticsService statisticsService;

	@Operation(summary = "Get platform statistics over a period",
			description = "Retrieves aggregated counts for Appointments, Visits, and Lab Results between a start and end date.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = StatisticsResponseDto.class))),
			@ApiResponse(responseCode = "400",
					description = "Invalid date range (e.g., end date before start date, future date, or missing parameters)",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "403", description = "Access denied. Only Admins can view statistics.",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping(ApiPaths.ADMIN_ENDPOINT)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseWrapper<StatisticsResponseDto> get(@Valid StatisticsPeriodRequestDto dto) {
		return ResponseWrapper.ok(statisticsService.getOverPeriod(dto));
	}

}
