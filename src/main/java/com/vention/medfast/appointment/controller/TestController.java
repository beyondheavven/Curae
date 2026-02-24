package com.vention.medfast.appointment.controller;

import com.vention.medfast.api.response.ErrorResponseDto;
import com.vention.medfast.appointment.dto.PatientLabHistoryDTO;
import com.vention.medfast.appointment.dto.TestCreateWithLabsDto;
import com.vention.medfast.appointment.service.TestService;
import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping(ApiPaths.TEST_ENDPOINT)
@RequiredArgsConstructor
@Tag(name = "Tests", description = "Endpoints for managing medical tests and lab results")
public class TestController {

	private final TestService testService;

	@PreAuthorize("hasRole('PATIENT')")
	@GetMapping("/history")
	@Operation(summary = "Get lab test history (PATIENT only)",
			description = "Returns the lab test history for the authenticated user")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lab test history returned",
					content = @Content(schema = @Schema(implementation = PatientLabHistoryDTO.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized",
					content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	public ResponseWrapper<Page<PatientLabHistoryDTO>> getLabHistory(@AuthenticationPrincipal CustomUserDetails user,
			@PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
		Page<PatientLabHistoryDTO> page = testService.getPatientLabHistory(user.getId(), pageable);
		return ResponseWrapper.ok(page);
	}

	@PreAuthorize("hasRole('DOCTOR')")
	@PostMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Create appointment with lab tests (DOCTOR only)",
			description = "Creates a lab appointment and related lab results")
	public void createWithLabs(@Valid @RequestBody TestCreateWithLabsDto request) {
		testService.createWithLabs(request);
	}

}
