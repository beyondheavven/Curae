package com.vention.medfast.globalsearch.controller;

import com.vention.medfast.api.response.ErrorResponseDto;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.globalsearch.dto.SearchRequest;
import com.vention.medfast.globalsearch.dto.SearchResponseDto;
import com.vention.medfast.globalsearch.service.SearchService;
import com.vention.medfast.wrapper.ResponseFactory;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.SEARCH_ENDPOINT)
@RequiredArgsConstructor
@Tag(name = "Search", description = "Endpoints for searching doctors and services across the application")
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "Perform structured search",
        description = "Searches for doctors and services using specific filters.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Search performed successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = SearchResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "No results found",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDto.class))) })
    @GetMapping
    public ResponseEntity<ResponseWrapper<SearchResponseDto>> search(@Valid @ParameterObject @ModelAttribute SearchRequest request) {

        SearchResponseDto result = searchService.performGlobalSearch(request);

        boolean isDoctorsEmpty = result.doctors() == null || result.doctors().isEmpty();
        boolean isServicesEmpty = result.services() == null || result.services().isEmpty();

        if (isDoctorsEmpty && isServicesEmpty) {
            throw new EntityNotFoundException("Your search returned no results");
        }

        return ResponseEntity.ok(ResponseFactory.ok(result));
    }
}
