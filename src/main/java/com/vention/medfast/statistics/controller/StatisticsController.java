package com.vention.medfast.statistics.controller;

import com.vention.medfast.api.response.ErrorResponseDto;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.statistics.dto.request.StatisticsPeriodRequestDto;
import com.vention.medfast.statistics.dto.response.ClinicStatisticsResponseDto;
import com.vention.medfast.statistics.dto.response.DoctorStatisticsResponseDto;
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
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.STATISTICS_ENDPOINT)
@Tag(name = "Statistics", description = "Endpoints for platform analytics and reporting.")
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

	@Operation(summary = "Get clinic statistics over a period",
			description = "Returns aggregated visit, online consultation, and lab statistics for a clinic between the given dates.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ClinicStatisticsResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Invalid date range or malformed parameters",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "403", description = "Access denied",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping("/clinics/{clinicId}/statistics")
	public ResponseWrapper<ClinicStatisticsResponseDto> getClinicStats(@PathVariable Long clinicId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		return ResponseWrapper.ok(statisticsService.getClinicStatOverPeriod(clinicId, startDate, endDate));
	}

	@Operation(summary = "Get doctor statistics over a period",
			description = "Returns visit and online consultation statistics for a doctor between the given dates.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = DoctorStatisticsResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Invalid date range or malformed parameters",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "403", description = "Access denied",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping("/doctor/{doctorId}/statistics")
	public ResponseWrapper<DoctorStatisticsResponseDto> getDoctorStats(@PathVariable Long doctorId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		return ResponseWrapper.ok(statisticsService.getDoctorStatsOverPeriod(doctorId, startDate, endDate));
	}

}
