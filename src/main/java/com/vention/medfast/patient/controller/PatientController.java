package com.vention.medfast.patient.controller;

import com.vention.medfast.api.response.ErrorResponseDto;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.patient.dto.PatientCreateDto;
import com.vention.medfast.patient.dto.PatientResponseDto;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.PATIENT_ENDPOINT)
@RequiredArgsConstructor
@Tag(name = "Patients", description = "Endpoints for managing patients")
public class PatientController {

	private final PatientService patientService;

	@Operation(summary = "Create patient")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Successfully created",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = PatientResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Validation error",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<PatientResponseDto> create(@Valid @RequestBody PatientCreateDto request) {
		PatientResponseDto response = patientService.create(request);
		return ResponseWrapper.withStatus(HttpStatus.CREATED, response);
	}

	@Operation(summary = "Get patient by ID")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Patient found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = PatientResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Patient not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR') or (hasRole('PATIENT') && #id == authentication.principal.patientId)")
	@GetMapping("/{id}")
	public ResponseWrapper<PatientResponseDto> getById(
			@Parameter(description = "ID of patient to find") @PathVariable(name = "id") Long id) {
		return ResponseWrapper.ok(patientService.getPatientById(id));
	}

}
