package com.vention.medfast.patient.controller;

import com.vention.medfast.api.response.ErrorResponseDto;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.patient.dto.PatientCreateDto;
import com.vention.medfast.patient.dto.PatientResponseDto;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.wrapper.ResponseFactory;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
	public ResponseEntity<ResponseWrapper<PatientResponseDto>> createPatient(
			@Valid @RequestBody PatientCreateDto request) {
		PatientResponseDto response = patientService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ResponseFactory.withStatus(HttpStatus.CREATED, response));
	}

	@Operation(summary = "Get patient by ID")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Patient found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = PatientResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Patient not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping("/{id}")
	public ResponseEntity<ResponseWrapper<PatientResponseDto>> getPatientById(
			@Parameter(description = "ID of patient to find") @PathVariable(name = "id") Long id) {
		return ResponseEntity.ok(ResponseFactory.ok(patientService.getPatientById(id)));
	}

}
