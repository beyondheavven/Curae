package com.vention.medfast.auth.controller;

import com.vention.medfast.auth.dto.*;
import com.vention.medfast.wrapper.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.vention.medfast.auth.service.*;
import com.vention.medfast.constants.ApiPaths;

@RestController
@RequestMapping(ApiPaths.AUTH_ENDPOINT)
@AllArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication and token management")
public class AuthController {

    private final AuthService authService;

    @Operation(
        summary = "Authentication",
        description = "Accepts email and password, returns access and refresh tokens upon successful authentication."
    )
    @PostMapping("/login")
    public ResponseWrapper<AuthResponseDto> login(@RequestBody AuthRequestDto request) {
        try {
            AuthResponseDto response = authService.login(request);
            return ResponseFactory.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseFactory.badRequest(ex.getMessage(), ErrorCode.AUTH_ERROR);
        }
    }

    @Operation(
        summary = "Refresh access token",
        description = "Accepts a refresh token and returns a new access token."
    )
    @PostMapping("/refresh")
    public ResponseWrapper<AuthResponseDto> refresh(@RequestBody RefreshTokenRequestDto request) {
        try {
            AuthResponseDto response = authService.refresh(request.getRefreshToken());
            return ResponseFactory.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseFactory.badRequest(ex.getMessage(), ErrorCode.AUTH_ERROR);
        }
    }

    @Operation(
        summary = "Logout",
        description = "Invalidates the provided refresh token, effectively logging the user out."
    )
    @PostMapping("/logout")
    @ResponseStatus
    public void logout(@RequestBody LogoutRequestDto request) {
        authService.logout(request.getRefreshToken());
    }
}
