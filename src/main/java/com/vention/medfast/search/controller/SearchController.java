package com.vention.medfast.search.controller;


import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.search.dto.SearchResponse;
import com.vention.medfast.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(ApiPaths.SEARCH_ENDPOINT)
@RequiredArgsConstructor
@Tag(name = "Search", description = "Endpoints for searching doctors and service")
public class SearchController {

    private SearchService searchService;

    @Operation(
        summary = "Search on the page",
        description = "Searches for doctors (by name, surname, specialisation) and services. Results are sorted alphabetically."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful search"),
        @ApiResponse(responseCode = "400", description = "Incorrect query parameters")
    })

    @GetMapping
    public ResponseEntity<SearchResponse> search(
        @Parameter(description = "Строка поиска (минимум 3 символа)", example = "cardio")
        @RequestParam("q") String query
    ) {
        return ResponseEntity.ok(searchService.performGlobalSearch(query));
    }
}
