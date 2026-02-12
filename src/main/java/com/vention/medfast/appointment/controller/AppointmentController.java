package com.vention.medfast.appointment.controller;

import com.vention.medfast.api.response.ErrorResponseDto;
import com.vention.medfast.appointment.dto.AppointmentCreateDto;
import com.vention.medfast.appointment.dto.AppointmentUpdateDto;
import com.vention.medfast.appointment.service.AppointmentService;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.APPOINTMENT_ENDPOINT)
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Endpoints for managing appointments")
public class AppointmentController {

	private final AppointmentService appointmentService;

	@Operation(summary = "Create appointment",
			description = "Creates appointment. Referral is optional and used only if provided")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Appointment created"),
			@ApiResponse(responseCode = "400", description = "Validation error",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Doctor or referral not found",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PostMapping
	@PreAuthorize("hasRole('PATIENT') && @securityService.canUseReferral(#dto.referralId)")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<Long> createAppointment(@Valid @RequestBody AppointmentCreateDto dto) {
		Long id = appointmentService.create(dto);
		return ResponseWrapper.withStatus(HttpStatus.CREATED, id);
	}

	@Operation(summary = "Confirm appointment", description = "Confirms previously created appointment draft")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Appointment confirmed"),
			@ApiResponse(responseCode = "400", description = "Invalid appointment state",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Appointment not found",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PostMapping("/{id}/confirm")
	@PreAuthorize("hasRole('PATIENT') && @securityService.isAppointmentAvailable(#id)")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void confirm(@PathVariable Long id) {
		appointmentService.confirm(id);
	}

	@PatchMapping("/{id}/cancel")
	@PreAuthorize("hasRole('ADMIN') or (hasAnyRole('PATIENT', 'DOCTOR') && @securityService.isAppointmentAvailableExtended(#id))")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void cancelAppointment(@PathVariable Long id) {
		appointmentService.cancelAppointment(id);
	}

	@Operation(summary = "Update appointment", description = "Updates appointment details")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Appointment updated"),
			@ApiResponse(responseCode = "400", description = "Invalid appointment state",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Appointment not found",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('PATIENT') && @securityService.isAppointmentAvailable(#id)")
	public ResponseWrapper<Long> update(@PathVariable Long id, @Valid @RequestBody AppointmentUpdateDto dto) {
		return ResponseWrapper.ok(appointmentService.update(dto, id));
	}

}
