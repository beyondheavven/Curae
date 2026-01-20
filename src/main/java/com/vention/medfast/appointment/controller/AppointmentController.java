package com.vention.medfast.appointment.controller;

import com.vention.medfast.api.response.ErrorResponseDto;
import com.vention.medfast.appointment.dto.AppointmentCreateDto;
import com.vention.medfast.appointment.dto.AppointmentCreateWithLabsDto;
import com.vention.medfast.appointment.dto.FirstAppointmentCreateDto;
import com.vention.medfast.appointment.dto.PatientLabHistoryDTO;
import com.vention.medfast.appointment.service.AppointmentService;
import com.vention.medfast.auth.core.CustomUserDetails;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

	@Operation(summary = "Create first appointment", description = "Creates first appointment without referral")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "First appointment created",
					content = @Content(schema = @Schema(implementation = Long.class))),
			@ApiResponse(responseCode = "400", description = "Validation error",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PostMapping("/first")
	@PreAuthorize("hasRole('PATIENT')")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<Long> createFirst(@Valid @RequestBody FirstAppointmentCreateDto dto) {
		Long id = appointmentService.createFirst(dto);
		return ResponseWrapper.withStatus(HttpStatus.CREATED, id);
	}

	@Operation(summary = "Create appointment",
			description = "Creates appointment draft. Referral is optional and used only if provided")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Appointment draft created"),
			@ApiResponse(responseCode = "400", description = "Validation error",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Doctor or referral not found",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PostMapping
	@PreAuthorize("hasRole('PATIENT')")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<Long> createDraft(@Valid @RequestBody AppointmentCreateDto dto) {
		Long id = appointmentService.createDraft(dto);
		return ResponseWrapper.withStatus(HttpStatus.CREATED, id);
	}

	@Operation(summary = "Confirm appointment", description = "Confirms previously created appointment draft")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Appointment confirmed"),
			@ApiResponse(responseCode = "400", description = "Invalid appointment state",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Appointment not found",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PostMapping("/{id}/confirm")
	@PreAuthorize("hasRole('PATIENT')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void confirm(@PathVariable Long id) {
		appointmentService.confirm(id);
	}

	@PreAuthorize("hasRole('PATIENT')")
	@GetMapping("/lab")
	@Operation(summary = "Get lab test history (PATIENT only)",
			description = "Returns the lab test history for the authenticated user")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lab test history returned",
					content = @Content(schema = @Schema(implementation = PatientLabHistoryDTO.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	public ResponseWrapper<Page<PatientLabHistoryDTO>> getLabHistory(@AuthenticationPrincipal CustomUserDetails user,
			@PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
		return ResponseWrapper.ok(appointmentService.getPatientLabHistory(user.getId(), pageable));
	}

	@PreAuthorize("hasRole('DOCTOR')")
	@PostMapping("/lab")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Create appointment with lab tests (DOCTOR only)",
			description = "Creates a lab appointment and related lab results")
	@ApiResponses({ @ApiResponse(responseCode = "204", description = "Appointment with labs created"),
			@ApiResponse(responseCode = "400", description = "Validation error",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "403", description = "Forbidden – requires DOCTOR role",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	public void createWithLabs(@Valid @RequestBody AppointmentCreateWithLabsDto request) {
		appointmentService.createWithLabs(request);
	}

}
