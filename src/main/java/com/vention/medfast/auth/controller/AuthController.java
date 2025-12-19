package com.vention.medfast.auth.controller;

import com.vention.medfast.auth.dto.AuthRequestDto;
import com.vention.medfast.auth.dto.AuthResponseDto;
import com.vention.medfast.auth.dto.LogoutRequestDto;
import com.vention.medfast.auth.dto.RefreshTokenRequestDto;
import com.vention.medfast.auth.service.AuthService;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.wrapper.ResponseFactory;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.AUTH_ENDPOINT)
@AllArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication and token management")
public class AuthController {

	private final AuthService authService;

	@Operation(summary = "Authentication",
			description = "Accepts email and password, returns access and refresh tokens upon successful authentication.")
	@PostMapping("/login")
	public ResponseEntity<ResponseWrapper<AuthResponseDto>> login(@Valid @RequestBody AuthRequestDto request) {
		AuthResponseDto response = authService.login(request);
		return ResponseEntity.ok(ResponseFactory.ok(response));
	}

	@Operation(summary = "Refresh access token",
			description = "Accepts a refresh token and returns a new access token.")
	@PostMapping("/refresh")
	public ResponseEntity<ResponseWrapper<AuthResponseDto>> refresh(
			@Valid @RequestBody RefreshTokenRequestDto request) {
		AuthResponseDto response = authService.refresh(request.getRefreshToken());

		return ResponseEntity.ok(ResponseFactory.ok(response));
	}

	@Operation(summary = "Logout",
			description = "Invalidates the provided refresh token, effectively logging the user out.")
	@PostMapping("/logout")
	@ResponseStatus
	public void logout(@RequestBody LogoutRequestDto request) {
		authService.logout(request.getRefreshToken());
	}

}
