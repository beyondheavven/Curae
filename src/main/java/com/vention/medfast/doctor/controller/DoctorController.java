package com.vention.medfast.doctor.controller;

import com.vention.medfast.api.response.ErrorResponseDto;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.doctor.dto.DoctorClinicAssignmentRequestDto;
import com.vention.medfast.doctor.dto.DoctorResponseDto;
import com.vention.medfast.doctor.dto.DoctorSearchDto;
import com.vention.medfast.doctor.dto.DoctorSearchResponseDto;
import com.vention.medfast.doctor.dto.DoctorUpdateDto;
import com.vention.medfast.doctor.service.DoctorService;
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
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.DOCTOR_ENDPOINT)
@Tag(name = "Doctors", description = "Endpoints for managing doctors")
public class DoctorController {

	private final DoctorService doctorService;

	@Operation(summary = "Search doctors",
			description = "Search by name, surname and specialization and optional filter by specialization and clinics.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Search results retrieved",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = DoctorSearchResponseDto.class))),
			@ApiResponse(responseCode = "500", description = "Internal server error",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping(ApiPaths.SEARCH_ENDPOINT)
	public ResponseWrapper<Page<DoctorSearchResponseDto>> search(
			@ParameterObject @ModelAttribute DoctorSearchDto searchDto, @ParameterObject @PageableDefault(size = 10,
					sort = { "user.firstName", "user.lastName" }, direction = Sort.Direction.ASC) Pageable pageable) {
		return ResponseWrapper.ok(doctorService.search(searchDto, pageable));
	}

	@Operation(summary = "Assign clinics to doctor",
			description = "Replaces the doctor's current clinic list with the provided set of clinic ids. If the list is empty, all clinics from doctor are removed.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Clinics assigned successfully",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = DoctorResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Validation error",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Doctor not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PutMapping("/{id}" + ApiPaths.CLINIC_ENDPOINT)
	public ResponseWrapper<DoctorResponseDto> setClinics(
			@Parameter(description = "ID of doctor to set clinic") @PathVariable Long id,
			@Valid @RequestBody DoctorClinicAssignmentRequestDto dto) {
		return ResponseWrapper.ok(doctorService.setClinics(id, dto));
	}

	@Operation(summary = "Get doctor by ID")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Doctor found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = DoctorResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Doctor not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping("/{id}")
	public ResponseWrapper<DoctorResponseDto> getById(
			@Parameter(description = "ID of doctor to find") @PathVariable Long id) {
		return ResponseWrapper.ok(doctorService.getById(id));
	}

	@Operation(summary = "Get all doctors")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "List of doctors retrieved",
			content = @Content(mediaType = "application/json",
					schema = @Schema(implementation = DoctorResponseDto.class))) })
	@GetMapping
	public ResponseWrapper<List<DoctorResponseDto>> getAll() {
		return ResponseWrapper.ok(doctorService.getAll());
	}

	@Operation(summary = "Update doctor profile")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Doctor updated successfully",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = DoctorResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Validation error",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Doctor not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PutMapping("/{id}")
	public ResponseWrapper<DoctorResponseDto> update(
			@Parameter(description = "ID of doctor to update") @PathVariable Long id,
			@Valid @RequestBody DoctorUpdateDto dto) {

		DoctorResponseDto updatedDoctor = doctorService.update(id, dto);
		return ResponseWrapper.ok(updatedDoctor);
	}

	@Operation(summary = "Delete doctor profile")
	@ApiResponses({ @ApiResponse(responseCode = "204", description = "Doctor deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Doctor not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@Parameter(description = "ID of doctor to delete") @PathVariable Long id) {
		doctorService.delete(id);
	}

}
