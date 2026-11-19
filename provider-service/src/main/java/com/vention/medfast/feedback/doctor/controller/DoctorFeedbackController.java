package com.vention.medfast.feedback.doctor.controller;

import com.vention.medfast.api.response.ErrorResponseDto;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.feedback.doctor.dto.DoctorFeedbackCreateDto;
import com.vention.medfast.feedback.doctor.dto.DoctorFeedbackSummaryDto;
import com.vention.medfast.feedback.doctor.service.DoctorFeedbackService;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
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
@RequestMapping(ApiPaths.FEEDBACK_ENDPOINT + ApiPaths.DOCTOR_ENDPOINT)
@RequiredArgsConstructor
@Tag(name = "Feedbacks", description = "Endpoints for managing doctor feedbacks")
public class DoctorFeedbackController {

	private final DoctorFeedbackService doctorFeedbackService;

	@Operation(summary = "Get feedback summary")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successfully retrieved feedback summary",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = DoctorFeedbackSummaryDto.class))),
			@ApiResponse(responseCode = "404", description = "Doctor not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping("/{doctorId}")
	public ResponseWrapper<DoctorFeedbackSummaryDto> getSummary(@PathVariable Long doctorId,
			@ParameterObject Pageable pageable) {
		return ResponseWrapper.ok(doctorFeedbackService.getFeedbackSummaryByDoctorId(doctorId, pageable));
	}

	@Operation(summary = "Create feedback for doctor")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Successfully created"),
			@ApiResponse(responseCode = "400", description = "Validation or business error",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PreAuthorize("hasRole('PATIENT')")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<Long> create(Principal principal, @Valid @RequestBody DoctorFeedbackCreateDto dto) {
		long id = doctorFeedbackService.create(Long.valueOf(principal.getName()), dto);
		return ResponseWrapper.withStatus(HttpStatus.CREATED, id);
	}

}