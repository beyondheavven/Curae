package com.vention.medfast.clinic.controller;

import com.vention.medfast.api.response.ErrorResponseDto;
import com.vention.medfast.clinic.dto.ClinicRequestDto;
import com.vention.medfast.clinic.dto.ClinicResponseDto;
import com.vention.medfast.clinic.service.ClinicService;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.CLINIC_ENDPOINT)
@RequiredArgsConstructor
@Tag(name = "Clinics", description = "Endpoints for managing clinics")
public class ClinicController {

	private final ClinicService clinicService;

	@Operation(summary = "Get all clinics")
	@GetMapping
	public ResponseWrapper<List<ClinicResponseDto>> getAll() {
		return ResponseWrapper.ok(clinicService.getAll());
	}

	@Operation(summary = "Get clinic by ID")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Clinic found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ClinicResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Clinic not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping("/{id}")
	public ResponseWrapper<ClinicResponseDto> getById(
			@Parameter(description = "ID of clinic to retrieve") @PathVariable Long id) {
		return ResponseWrapper.ok(clinicService.getById(id));
	}

	@Operation(summary = "Create clinic")
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<Long> create(@Valid @RequestBody ClinicRequestDto dto) {
		return ResponseWrapper.withStatus(HttpStatus.CREATED, clinicService.create(dto));
	}

	@Operation(summary = "Update clinic")
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseWrapper<Long> update(@Parameter(description = "ID of clinic to update") @PathVariable Long id,
			@Valid @RequestBody ClinicRequestDto dto) {
		return ResponseWrapper.ok(clinicService.update(id, dto));
	}

	@Operation(summary = "Delete clinic")
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@Parameter(description = "ID of clinic to delete") @PathVariable Long id) {
		clinicService.delete(id);
	}

}
