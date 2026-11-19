package com.vention.curae.patient.controller;

import com.vention.curae.constants.ApiPaths;
import com.vention.curae.patient.dto.PatientResponseDto;
import com.vention.curae.patient.service.PatientService;
import com.vention.curae.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.PATIENT_ENDPOINT)
@RequiredArgsConstructor
@Tag(name = "Patients", description = "Endpoints for managing patients")
public class PatientController {

	private final PatientService patientService;

	@Operation(summary = "Get patient by ID")
	@GetMapping("/{id}")
	public ResponseWrapper<PatientResponseDto> getById(
			@Parameter(description = "ID of patient to find") @PathVariable("id") Long id,
			Authentication authentication) {
		boolean isAdminOrDoctor = authentication.getAuthorities()
			.stream()
			.anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()) || "ROLE_DOCTOR".equals(a.getAuthority()));
		if (!isAdminOrDoctor) {
			Long userId = Long.valueOf(authentication.getName());
			if (!patientService.isOwnedByUser(id, userId)) {
				throw new AccessDeniedException("Access denied");
			}
		}
		return ResponseWrapper.ok(patientService.getPatientById(id));
	}

}
