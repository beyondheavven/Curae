package com.vention.curae.appointment.controller;

import com.vention.curae.appointment.dto.StatusStatisticDto;
import com.vention.curae.appointment.service.StatisticsService;
import com.vention.curae.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "Appointment statistics")
public class StatisticsController {

	private final StatisticsService statisticsService;

	@PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
	@GetMapping("/appointments/status")
	@Operation(summary = "Appointment counts by status")
	public ResponseWrapper<List<StatusStatisticDto>> appointmentsByStatus() {
		return ResponseWrapper.ok(statisticsService.appointmentStatusStatistics());
	}

	@PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
	@GetMapping("/visits/clinic")
	@Operation(summary = "Visit counts by clinic")
	public ResponseWrapper<List<StatusStatisticDto>> visitsByClinic() {
		return ResponseWrapper.ok(statisticsService.visitStatisticsByClinic());
	}

	@PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
	@GetMapping("/visits/doctor")
	@Operation(summary = "Visit counts by doctor")
	public ResponseWrapper<List<StatusStatisticDto>> visitsByDoctor() {
		return ResponseWrapper.ok(statisticsService.visitStatisticsByDoctor());
	}

	@PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
	@GetMapping("/tests/clinic")
	@Operation(summary = "Test counts by clinic")
	public ResponseWrapper<List<StatusStatisticDto>> testsByClinic() {
		return ResponseWrapper.ok(statisticsService.testStatisticsByClinic());
	}

}
