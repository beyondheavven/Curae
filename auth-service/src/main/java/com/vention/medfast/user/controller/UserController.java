package com.vention.medfast.user.controller;

import com.vention.medfast.api.response.ErrorResponseDto;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.user.dto.UserResponseDto;
import com.vention.medfast.user.dto.UserUpdateRequestDto;
import com.vention.medfast.user.service.UserService;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.USER_ENDPOINT)
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for retrieving user profiles.")
public class UserController {

	private final UserService userService;

	@Operation(summary = "Get user by id")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "User found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = UserResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "User not found with provided ID",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN') || (hasAnyRole('PATIENT', 'DOCTOR') && #id == authentication.principal.id)")
	public ResponseWrapper<UserResponseDto> getById(
			@Parameter(description = "ID of user to be retrieved") @PathVariable Long id) {
		return ResponseWrapper.ok(userService.getById(id));
	}

	@Operation(summary = "Get list of users")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users",
			content = @Content(mediaType = "application/json",
					schema = @Schema(implementation = UserResponseDto.class))), })
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseWrapper<Page<UserResponseDto>> getAll(
			@Parameter(description = "Pagination") @PageableDefault(size = 20) Pageable pageable) {
		return ResponseWrapper.ok(userService.getAll(pageable));
	}

	@Operation(summary = "Update user profile as admin")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "User updated successfully",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = UserResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Validation error",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "User not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "409", description = "Email already exists",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseWrapper<UserResponseDto> update(
			@Parameter(description = "ID of user to update") @PathVariable Long id,
			@Valid @RequestBody UserUpdateRequestDto dto) {
		return ResponseWrapper.ok(userService.updateUser(id, dto));
	}

}
