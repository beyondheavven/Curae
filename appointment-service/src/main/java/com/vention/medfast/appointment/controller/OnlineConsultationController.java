package com.vention.medfast.appointment.controller;

import com.vention.medfast.appointment.dto.CreateOnlineConsultationRequest;
import com.vention.medfast.appointment.dto.OnlineConsultationDetails;
import com.vention.medfast.appointment.dto.UpdateOnlineConsultationRequest;
import com.vention.medfast.appointment.service.OnlineConsultationService;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/online-consultation")
@RequiredArgsConstructor
@Tag(name = "Online consultations", description = "Endpoints for online consultations")
public class OnlineConsultationController {

	private final OnlineConsultationService consultationService;

	@PreAuthorize("hasAnyRole('ADMIN','PATIENT','DOCTOR')")
	@GetMapping("/{id}")
	@Operation(summary = "Get consultation details")
	public ResponseWrapper<OnlineConsultationDetails> getDetails(@PathVariable Long id) {
		return ResponseWrapper.ok(consultationService.getDetails(id));
	}

	@PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create online consultation")
	public ResponseWrapper<Long> create(@Valid @RequestBody CreateOnlineConsultationRequest dto) {
		return ResponseWrapper.withStatus(HttpStatus.CREATED, consultationService.create(dto));
	}

	@PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
	@PatchMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Update online consultation")
	public void update(@Valid @RequestBody UpdateOnlineConsultationRequest dto) {
		consultationService.update(dto);
	}

	@PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Delete online consultation")
	public void delete(@PathVariable Long id) {
		consultationService.deleteById(id);
	}

}
