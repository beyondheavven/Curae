package com.vention.curae.country.controller;

import com.vention.curae.constants.ApiPaths;
import com.vention.curae.country.dto.CountryResponseDto;
import com.vention.curae.country.service.CountryService;
import com.vention.curae.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.COUNTRY_ENDPOINT)
@RequiredArgsConstructor
@Tag(name = "Countries", description = "Endpoint for reference data for Countries and Citizenship")
public class CountryController {

	private final CountryService countryService;

	@Operation(summary = "Get all countries")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Page of countries retrieved successfully",
			content = @Content(mediaType = "application/json",
					schema = @Schema(implementation = CountryResponseDto.class))) })
	@GetMapping
	public ResponseWrapper<Page<CountryResponseDto>> getAllCountries(
			@ParameterObject @PageableDefault(size = 20, sort = "name") Pageable pageable) {
		return ResponseWrapper.ok(countryService.getAll(pageable));
	}

}
