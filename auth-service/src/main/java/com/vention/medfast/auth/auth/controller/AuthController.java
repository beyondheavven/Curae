package com.vention.medfast.auth.auth.controller;

import com.vention.medfast.auth.auth.dto.AuthResponseDto;
import com.vention.medfast.auth.auth.dto.LogoutRequestDto;
import com.vention.medfast.auth.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.AUTH_ENDPOINT)
@AllArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication and token management")
public class AuthController {

	private final AuthService authService;

	@Operation(summary = "Authentication",
			description = "Accepts email and password, returns access and refresh tokens upon successful authentication.")
	@PostMapping("/login")
	public ResponseWrapper<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto request) {
		AuthResponseDto response = authService.login(request);
		return ResponseWrapper.ok(response);
	}

	@Operation(summary = "Refresh access token",
			description = "Accepts a refresh token and returns a new access token.")
	@PostMapping("/refresh")
	public ResponseWrapper<AuthResponseDto> refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
		AuthResponseDto response = authService.refresh(request.refreshToken());

		return ResponseWrapper.ok(response);
	}

	@Operation(summary = "Logout",
			description = "Invalidates the provided refresh token, effectively logging the user out.")
	@PostMapping("/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void logout(@RequestBody LogoutRequestDto request) {
		authService.logout(request.refreshToken());
	}

}
