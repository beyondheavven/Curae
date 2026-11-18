package com.vention.medfast.specialization.controller;

import com.vention.medfast.api.response.ErrorResponseDto;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.specialization.dto.SpecializationCreateDto;
import com.vention.medfast.specialization.dto.SpecializationResponseDto;
import com.vention.medfast.specialization.service.SpecializationService;
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

@RequestMapping(ApiPaths.SPECIALIZATION_ENDPOINT)
@RestController
@RequiredArgsConstructor
@Tag(name = "Specializations", description = "Endpoints for managing doctor specializations")
public class SpecializationController {

	private final SpecializationService specializationService;

	@Operation(summary = "Get all specializations")
	@GetMapping
	public ResponseWrapper<List<SpecializationResponseDto>> getAll() {
		return ResponseWrapper.ok(specializationService.getAll());
	}

	@Operation(summary = "Get specialization by ID")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Specialization found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = SpecializationResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Specialization not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping("/{id}")
	public ResponseWrapper<SpecializationResponseDto> getById(
			@Parameter(description = "ID of specialization to find") @PathVariable Long id) {
		return ResponseWrapper.ok(specializationService.getById(id));
	}

	@Operation(summary = "Create specialization")
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<Long> create(@Valid @RequestBody SpecializationCreateDto dto) {
		return ResponseWrapper.withStatus(HttpStatus.CREATED, specializationService.create(dto));
	}

	@Operation(summary = "Delete a specialization")
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		specializationService.deleteById(id);
	}

	@Operation(summary = "Update an existing specialization")
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseWrapper<Long> update(@PathVariable Long id, @Valid @RequestBody SpecializationCreateDto dto) {
		return ResponseWrapper.ok(specializationService.update(id, dto));
	}

}