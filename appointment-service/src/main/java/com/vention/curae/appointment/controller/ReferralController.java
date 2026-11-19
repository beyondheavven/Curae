package com.vention.curae.appointment.controller;

import com.vention.curae.appointment.dto.ReferralCreateDto;
import com.vention.curae.appointment.dto.ReferralResponseDto;
import com.vention.curae.appointment.security.AppointmentSecurityService;
import com.vention.curae.appointment.service.ReferralService;
import com.vention.curae.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prescriptions")
@RequiredArgsConstructor
@Tag(name = "Referrals", description = "Endpoints for prescriptions")
public class ReferralController {

	private final ReferralService referralService;

	private final AppointmentSecurityService securityService;

	@PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create referral")
	public ResponseWrapper<Long> create(@Valid @RequestBody ReferralCreateDto dto) {
		return ResponseWrapper.withStatus(HttpStatus.CREATED, referralService.create(dto));
	}

	@PreAuthorize("hasRole('PATIENT')")
	@GetMapping
	@Operation(summary = "Get active referrals")
	public ResponseWrapper<List<ReferralResponseDto>> getActive() {
		return ResponseWrapper.ok(referralService.getActiveByPatient(securityService.currentPatientId()));
	}

	@PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
	@PatchMapping("/{id}/use")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Mark referral as used")
	public void markUsed(@PathVariable Long id) {
		referralService.markUsed(id);
	}

}
