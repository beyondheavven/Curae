package com.vention.medfast.appointment.controller;

import com.vention.medfast.api.response.ErrorResponseDto;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.appointment.dto.VisitCreateDto;
import com.vention.medfast.appointment.dto.VisitListItemDto;
import com.vention.medfast.appointment.model.VisitType;
import com.vention.medfast.appointment.service.VisitService;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping(ApiPaths.VISIT_ENDPOINT)
@RequiredArgsConstructor
@Tag(name = "Visits", description = "Endpoints for managing visits")
public class VisitController {

	private final VisitService visitService;

	@Operation(summary = "Create visit")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Successfully created",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class))),
			@ApiResponse(responseCode = "400", description = "Validation error",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<Long> create(@Valid @RequestBody VisitCreateDto dto,
			@RequestParam(name = "patientId") Long patientId) {
		long id = visitService.create(patientId, dto);
		return ResponseWrapper.withStatus(HttpStatus.CREATED, id);
	}

	@Operation(summary = "Get all visits")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successfully retrieved slice of visits",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = VisitListItemDto.class))),
			@ApiResponse(responseCode = "204", description = "No visits found") })
	@PreAuthorize("hasRole('PATIENT')")
	@GetMapping
	public ResponseWrapper<Slice<VisitListItemDto>> getAll(
			@RequestParam(name = "type", defaultValue = "UPCOMING") VisitType type,
			@ParameterObject Pageable pageable) {

		return ResponseWrapper.ok(visitService.findAllForPatient(type, pageable));
	}

}
