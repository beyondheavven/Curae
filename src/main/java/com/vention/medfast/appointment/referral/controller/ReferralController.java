package com.vention.medfast.appointment.referral.controller;

import com.vention.medfast.api.response.ErrorResponseDto;
import com.vention.medfast.appointment.referral.dto.ReferralCreateDto;
import com.vention.medfast.appointment.referral.dto.ReferralResponseDto;
import com.vention.medfast.appointment.referral.service.ReferralService;
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
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(ApiPaths.PRESCRIPTION_ENDPOINT)
@RequiredArgsConstructor
@Tag(name = "Prescriptions", description = "Endpoints for managing prescriptions (referrals)")
public class ReferralController {

	private final ReferralService referralService;

	@Operation(summary = "Create prescription", description = "Doctor issues a prescription for patient")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Prescription created",
					content = @Content(schema = @Schema(implementation = Long.class))),
			@ApiResponse(responseCode = "400", description = "Validation error",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PreAuthorize("hasRole('DOCTOR')")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	public ResponseWrapper<Long> create(@Valid @RequestBody ReferralCreateDto dto) {
		Long id = referralService.create(dto);
		return ResponseWrapper.withStatus(HttpStatus.CREATED, id);
	}

	@Operation(summary = "Get active prescriptions by patient",
			description = "Returns all active prescriptions for a patient")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Prescriptions retrieved"),
			@ApiResponse(responseCode = "400", description = "Invalid request",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
	@GetMapping
	public ResponseWrapper<List<ReferralResponseDto>> getActiveByPatient(@RequestParam Long patientId) {
		List<ReferralResponseDto> result = referralService.getActiveByPatient(patientId);
		return ResponseWrapper.ok(result);
	}

	@Operation(summary = "Mark prescription as used",
			description = "Marks prescription as used after appointment confirmation")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Prescription marked as used"),
			@ApiResponse(responseCode = "400", description = "Invalid prescription state",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Prescription not found",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PreAuthorize("hasRole('DOCTOR')")
	@PatchMapping("/{id}/use")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void markUsed(@PathVariable Long id) {
		referralService.markUsed(referralService.getActiveById(id));
	}

}
