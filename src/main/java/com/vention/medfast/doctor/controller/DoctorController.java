package com.vention.medfast.doctor.controller;

import com.vention.medfast.api.response.ErrorResponseDto;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.doctor.dto.DoctorSearchResponseDto;
import com.vention.medfast.doctor.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.DOCTOR_ENDPOINT)
public class DoctorController {

	private final DoctorService doctorService;

	@Operation(summary = "Search doctors", description = "Search by name, surname or specialization")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Search results retrieved",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = DoctorSearchResponseDto.class))),
			@ApiResponse(responseCode = "500", description = "Internal server error",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping()
	public ResponseEntity<Page<DoctorSearchResponseDto>> search(
			@Parameter(description = "Search query text (part of name, surname or specialization)") @RequestParam(
					required = true) String query,
			@ParameterObject @PageableDefault(size = 10, sort = { "user.firstName", "user.lastName" },
					direction = Sort.Direction.ASC) Pageable pageable) {
		Page<DoctorSearchResponseDto> result = doctorService.search(query, pageable);

		return ResponseEntity.ok((result));
	}

}
