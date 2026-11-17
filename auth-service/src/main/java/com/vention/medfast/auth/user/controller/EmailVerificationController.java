package com.vention.medfast.auth.user.controller;

import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.email.service.EmailVerificationTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(ApiPaths.AUTH_ENDPOINT)
@Tag(name = "Email Verification", description = "Endpoints for email verification and confirmation")
public class EmailVerificationController {

	private final EmailVerificationTokenService emailVerificationTokenService;

	@Operation(summary = "Verify email by token",
			description = "Verifies user's email address using a verification token sent via email",
			responses = { @ApiResponse(responseCode = "200", description = "Email successfully verified"),
					@ApiResponse(responseCode = "400", description = "Invalid or expired token", content = @Content),
					@ApiResponse(responseCode = "404", description = "Token not found", content = @Content) })
	@GetMapping("/verify")
	public void verify(@Parameter(description = "Email verification token", required = true,
			example = "AeeFL5k6fiGdNMKHw1tvROlC8RuFJXQrWqnbH2IY44E") @RequestParam String token) {
		emailVerificationTokenService.verify(token);
	}

}
