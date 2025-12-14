package com.vention.medfast.controller;

import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.dto.AuthRequest;
import com.vention.medfast.dto.AuthResponse;
import com.vention.medfast.dto.LogoutRequest;
import com.vention.medfast.dto.RefreshTokenRequest;
import com.vention.medfast.service.AuthService;
import com.vention.medfast.wrapper.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "Endpoitns for user authentication and token management")
public class AuthController {

	private final AuthService authService;

	@Operation(summary = "Authentication",
			description = "Accepts email and password, returns access and refresh tokens upon successful authentication.")
	@PostMapping("/login")
    public ResponseWrapper<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseFactory.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseFactory.badRequest(ex.getMessage(), ErrorCode.AUTH_ERROR);
        }
    }

	@Operation(summary = "Refresh of the access token",
			description = "Accepts a refresh token and returns a new access token.")
	@PostMapping("/refresh")
    public ResponseWrapper<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        try {
            AuthResponse response = authService.refresh(request.getRefreshToken());
            return ResponseFactory.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseFactory.badRequest(ex.getMessage(), ErrorCode.AUTH_ERROR);
        }
    }

	@Operation(summary = "Logout",
			description = "Invalidates the provided refresh token, effectively logging the user out.")
	@PostMapping("/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void logout(@RequestBody LogoutRequest request) {
		authService.logout(request.getRefreshToken());
	}
}
