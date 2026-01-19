package com.vention.medfast.appointment.controller;

import com.vention.medfast.api.response.ErrorResponseDto;
import com.vention.medfast.appointment.dto.AppointmentCreateWithLabsDto;
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
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.APPOINTMENT_ENDPOINT)
@AllArgsConstructor
@Tag(name = "Appointments", description = "Endpoints for appointment-related operations")
public class AppointmentController {

	private final AppointmentService appointmentService;

	@PreAuthorize("hasRole('PATIENT')")
	@GetMapping("/lab")
	@Operation(summary = "Get lab test history (PATIENT only)",
			description = "Returns the lab test history for the authenticated user. "
					+ "For a quick demo, login as `john.smith@mail.com` and use the issued access token.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lab test history returned",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = PatientLabHistoryDTO.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	public ResponseWrapper<Page<PatientLabHistoryDTO>> getLabHistory(@AuthenticationPrincipal CustomUserDetails user,
			@PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
		return ResponseWrapper.ok(appointmentService.getPatientLabHistory(user.getId(), pageable));
	}

	@PreAuthorize("hasRole('DOCTOR')")
	@PostMapping("/lab")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Create appointment with lab tests (DOCTOR only)",
			description = "Creates an appointment along with its lab tests. Only users with role DOCTOR can call this endpoint.")
	@ApiResponses({ @ApiResponse(responseCode = "204", description = "Appointment with labs created"),
			@ApiResponse(responseCode = "400", description = "Validation error",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "403", description = "Forbidden – requires DOCTOR role",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	public void createWithLabs(@Valid @RequestBody AppointmentCreateWithLabsDto request) {
		appointmentService.createWithLabs(request);
	}

}
