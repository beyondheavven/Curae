package com.vention.medfast.search.controller;

import com.vention.medfast.api.response.ErrorResponseDto;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.search.dto.SearchResponse;
import com.vention.medfast.search.service.SearchService;
import com.vention.medfast.wrapper.ResponseFactory;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.SEARCH_ENDPOINT)
@RequiredArgsConstructor
@Tag(name = "Global Search", description = "Endpoints for searching doctors and services across the application")
public class SearchController {

	private final SearchService searchService;

	@Operation(summary = "Perform global search",
			description = "Searches for doctors and services. Throws 404 if no matches found.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Search performed successfully",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = SearchResponse.class))),
			@ApiResponse(responseCode = "404", description = "No results found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping
	public ResponseEntity<ResponseWrapper<SearchResponse>> search(@Parameter(description = "Search keyword",
			example = "Dentist") @RequestParam(value = "query", required = false) String query) {

		SearchResponse result = searchService.performGlobalSearch(query);

		boolean isDoctorsEmpty = result.doctors() == null || result.doctors().isEmpty();
		boolean isServicesEmpty = result.services() == null || result.services().isEmpty();

		if (isDoctorsEmpty && isServicesEmpty) {
			throw new EntityNotFoundException("Your search returned no results");
		}

		return ResponseEntity.ok(ResponseFactory.ok(result));
	}

}
