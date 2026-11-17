package com.vention.medfast.auth.password.controller;

import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.password.dto.ChangePasswordRequestDto;
import com.vention.medfast.password.dto.PasswordResetConfirmDto;
import com.vention.medfast.password.dto.PasswordResetRequestDto;
import com.vention.medfast.password.dto.PasswordResetVerifyDto;
import com.vention.medfast.password.dto.PasswordResetVerifyResponseDto;
import com.vention.medfast.password.dto.SetPasswordRequestDto;
import com.vention.medfast.password.service.PasswordManagementService;
import com.vention.medfast.password.service.PasswordResetService;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Account", description = "Endpoints for account password management")
@RestController
@RequestMapping(ApiPaths.ACCOUNT_ENDPOINT)
@AllArgsConstructor
public class PasswordController {

	private final PasswordManagementService passwordManagementService;

	private final PasswordResetService passwordResetService;

	@Operation(summary = "Password change endpoint",
			description = "Endpoint for changing password for the authenticated user")
	@PostMapping(ApiPaths.CHANGE_PASSWORD_ENDPOINT)
	public ResponseWrapper<ChangePasswordRequestDto> changePassword(
			@Valid @RequestBody ChangePasswordRequestDto request,
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		passwordManagementService.changePassword(userDetails.getId(), request.getCurrentPassword(),
				request.getNewPassword(), request.getRepeatPassword());
		return ResponseWrapper.ok(request);
	}

	@Operation(summary = "Password change request endpoint",
			description = "Password change request endpoint which sends an email with the code to the specified email address.")
	@PostMapping(ApiPaths.RESET_PASSWORD_REQUEST_ENDPOINT)
	public ResponseWrapper<PasswordResetRequestDto> requestReset(@Valid @RequestBody PasswordResetRequestDto request) {
		passwordResetService.sendResetCode(request.getEmail());

		return ResponseWrapper.ok(request);
	}

	@Operation(summary = "Verifies code from an email",
			description = "Endpoint for the code verification which user gets from an email. Returns one-time token for password resetting")
	@PostMapping(ApiPaths.RESET_PASSWORD_VERIFY_ENDPOINT)
	public ResponseWrapper<PasswordResetVerifyResponseDto> verifyCode(
			@Valid @RequestBody PasswordResetVerifyDto request) {
		String resetToken = passwordResetService.verifyCode(request.getEmail(), request.getCode());

		return ResponseWrapper.ok(new PasswordResetVerifyResponseDto(resetToken));
	}

	@Operation(summary = "Resets password for user",
			description = "Endpoint for resetting a password using a one-time token.")
	@PostMapping(ApiPaths.RESET_PASSWORD_CONFIRM_ENDPOINT)
	public ResponseWrapper<PasswordResetConfirmDto> confirmReset(@Valid @RequestBody PasswordResetConfirmDto request) {
		passwordManagementService.resetPassword(request.getResetToken(), request.getNewPassword(),
				request.getRepeatPassword());

		return ResponseWrapper.ok(request);
	}

	@Operation(summary = "Resets password for already logged-in user",
			description = "Endpoint for resetting a password for already authenticated user.")
	@PostMapping(ApiPaths.SET_PASSWORD_ENDPOINT)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void setPassword(@RequestBody @Valid SetPasswordRequestDto request) {
		passwordManagementService.setPassword(request);
	}

}
