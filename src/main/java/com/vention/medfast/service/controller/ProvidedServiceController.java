package com.vention.medfast.service.controller;

import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.service.dto.ProvidedServiceCreateDto;
import com.vention.medfast.service.dto.ProvidedServiceResponseDto;
import com.vention.medfast.service.service.ProvidedServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;


import java.util.List;

@RequestMapping(ApiPaths.SERVICE_ENDPOINT)
@RestController
@RequiredArgsConstructor
@Tag(name = "Services", description = "Endpoints for managing services provided by doctors")
public class ProvidedServiceController {

	private final ProvidedServiceService providedServiceService;

	@Operation(summary = "Get all services")
	@ApiResponses({ @ApiResponse(responseCode = "200"),
			@ApiResponse(responseCode = "204", description = "No services found") })
	@GetMapping
	public ResponseEntity<List<ProvidedServiceResponseDto>> getAllServices() {
		List<ProvidedServiceResponseDto> services = providedServiceService.getAll();

		if (services.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(services);
	}

	@Operation(summary = "Get service by ID")
	@ApiResponses({ @ApiResponse(responseCode = "200"),
			@ApiResponse(responseCode = "404", description = "Service not found") })
	@GetMapping("/{id}")
	public ProvidedServiceResponseDto getServiceById(
			@Parameter(description = "ID of service to find") @PathVariable(name = "id") Long id) {
		return providedServiceService.getById(id);
	}

	@Operation(summary = "Create service")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Successfully created"),
			@ApiResponse(responseCode = "400", description = "Validation error") })
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Long createSpecialization(@Valid @RequestBody ProvidedServiceCreateDto dto) {
		return providedServiceService.create(dto);
	}

	@Operation(summary = "Delete a service")
	@ApiResponses({ @ApiResponse(responseCode = "204", description = "Service successfully deleted"),
			@ApiResponse(responseCode = "404", description = "Service not found") })
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSpecialization(@Parameter(description = "ID of the service to delete",
			required = true) @PathVariable(name = "id") Long id) {
		providedServiceService.deleteById(id);
	}

	@Operation(summary = "Update an existing service")
	@ApiResponses({ @ApiResponse(responseCode = "200"),
			@ApiResponse(responseCode = "400", description = "Validation error"),
			@ApiResponse(responseCode = "404", description = "Service not found") })
	@PutMapping("/{id}")
	public long updateSpecialization(
			@Parameter(description = "ID of the service to update", required = true) @PathVariable(name = "id") Long id,
			@Parameter(description = "Fields to update") @Valid @RequestBody ProvidedServiceCreateDto dto) {
		return providedServiceService.update(id, dto);
	}

	@Operation(summary = "Search services", description = "Search services by name with pagination")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved search results")
	@GetMapping("/search")
	public ResponseEntity<Page<ProvidedServiceResponseDto>> searchServices(
			@Parameter(description = "Search query text (searches in service name)") @RequestParam(
					required = false) String query,
			@ParameterObject @PageableDefault(size = 10, sort = "name",
					direction = Sort.Direction.ASC) Pageable pageable) {
		return ResponseEntity.ok(providedServiceService.search(query, pageable));
	}

}
