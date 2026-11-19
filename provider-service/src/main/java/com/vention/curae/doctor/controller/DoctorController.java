package com.vention.curae.doctor.controller;

import com.vention.curae.api.response.ErrorResponseDto;
import com.vention.curae.constants.ApiPaths;
import com.vention.curae.doctor.dto.DoctorClinicAssignmentRequestDto;
import com.vention.curae.doctor.dto.DoctorResponseDto;
import com.vention.curae.doctor.dto.DoctorSearchDto;
import com.vention.curae.doctor.dto.DoctorSearchResponseDto;
import com.vention.curae.doctor.dto.DoctorUpdateDto;
import com.vention.curae.doctor.service.DoctorService;
import com.vention.curae.wrapper.ResponseWrapper;
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
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.DOCTOR_ENDPOINT)
@Tag(name = "Doctors", description = "Endpoints for managing doctors")
public class DoctorController {

	private final DoctorService doctorService;

	@Operation(summary = "Search doctors",
			description = "Search by name, surname and specialization and optional filter by specialization and clinics.")
	@GetMapping(ApiPaths.SEARCH_ENDPOINT)
	public ResponseWrapper<Page<DoctorSearchResponseDto>> search(
			@ParameterObject @ModelAttribute DoctorSearchDto searchDto,
			@ParameterObject @PageableDefault(size = 10, sort = { "userSnapshot.firstName", "userSnapshot.lastName" },
					direction = Sort.Direction.ASC) Pageable pageable) {
		return ResponseWrapper.ok(doctorService.search(searchDto, pageable));
	}

	@Operation(summary = "Assign clinics to doctor")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Clinics assigned successfully",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = DoctorResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Doctor not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PutMapping("/{id}" + ApiPaths.CLINIC_ENDPOINT)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseWrapper<DoctorResponseDto> setClinics(@PathVariable Long id,
			@Valid @RequestBody DoctorClinicAssignmentRequestDto dto) {
		return ResponseWrapper.ok(doctorService.setClinics(id, dto));
	}

	@Operation(summary = "Get doctor by ID")
	@GetMapping("/{id}")
	public ResponseWrapper<DoctorResponseDto> getById(
			@Parameter(description = "ID of doctor to find") @PathVariable Long id) {
		return ResponseWrapper.ok(doctorService.getById(id));
	}

	@Operation(summary = "Get all doctors")
	@GetMapping
	public ResponseWrapper<List<DoctorResponseDto>> getAll() {
		return ResponseWrapper.ok(doctorService.getAll());
	}

	@Operation(summary = "Update doctor profile")
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseWrapper<DoctorResponseDto> update(@PathVariable Long id, @Valid @RequestBody DoctorUpdateDto dto) {
		return ResponseWrapper.ok(doctorService.update(id, dto));
	}

	@Operation(summary = "Delete doctor profile")
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		doctorService.delete(id);
	}

}