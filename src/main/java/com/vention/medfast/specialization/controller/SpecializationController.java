package com.vention.medfast.specialization.controller;

import com.vention.medfast.api.response.ErrorResponseDto;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.specialization.dto.SpecializationCreateDto;
import com.vention.medfast.specialization.dto.SpecializationResponseDto;
import com.vention.medfast.specialization.service.SpecializationService;
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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successfully retrieved list of specializations",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = SpecializationResponseDto.class))),
			@ApiResponse(responseCode = "204", description = "No specializations found") })
	@GetMapping
	public ResponseEntity<ResponseWrapper<List<SpecializationResponseDto>>> getAllSpecializations() {
		List<SpecializationResponseDto> specializations = specializationService.getAll();

		if (specializations.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
				.body(ResponseFactory.withStatus(HttpStatus.NO_CONTENT, List.of()));
		}

		return ResponseEntity.ok(ResponseFactory.ok(specializations));
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
	public ResponseEntity<ResponseWrapper<SpecializationResponseDto>> getSpecializationById(
			@Parameter(description = "ID of specialization to find") @PathVariable Long id) {
		return ResponseEntity.ok(ResponseFactory.ok(specializationService.getById(id)));
	}

	@Operation(summary = "Create specialization")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Successfully created",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = SpecializationResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Validation error",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PostMapping
	public ResponseEntity<ResponseWrapper<Long>> createSpecialization(@Valid @RequestBody SpecializationCreateDto dto) {
		long id = specializationService.create(dto);

		return ResponseEntity.status(HttpStatus.CREATED).body(ResponseFactory.withStatus(HttpStatus.CREATED, id));
	}

	@Operation(summary = "Delete a specialization")
	@ApiResponses({ @ApiResponse(responseCode = "204", description = "Specialization successfully deleted"),
			@ApiResponse(responseCode = "404", description = "Specialization not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSpecialization(
			@Parameter(description = "ID of the specialization to delete", required = true) @PathVariable Long id) {
		specializationService.deleteById(id);
	}

	@Operation(summary = "Update an existing specialization")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Specialization successfully updated",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = SpecializationResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Validation error",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Specialization not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PutMapping("/{id}")
	public ResponseEntity<ResponseWrapper<Long>> updateSpecialization(
			@Parameter(description = "ID of the specialization to update", required = true) @PathVariable Long id,
			@Parameter(description = "Fields to update") @Valid @RequestBody SpecializationCreateDto dto) {
		long updatedId = specializationService.update(id, dto);
		return ResponseEntity.ok(ResponseFactory.ok(updatedId));
	}

}
