package com.vention.medfast.password.controller;

import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.password.dto.ChangePasswordRequestDto;
import com.vention.medfast.password.dto.PasswordResetRequestDto;
import com.vention.medfast.password.dto.PasswordResetVerifyResponseDto;
import com.vention.medfast.password.dto.PasswordResetVerifyDto;
import com.vention.medfast.password.dto.PasswordResetConfirmDto;
import com.vention.medfast.password.dto.SetPasswordRequestDto;
import com.vention.medfast.password.service.PasswordManagementService;
import com.vention.medfast.password.service.PasswordResetService;
import com.vention.medfast.wrapper.ResponseFactory;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping(ApiPaths.ACCOUNT_ENDPOINT)
@AllArgsConstructor
@Tag(name = "Account", description = "Endpoints for account password management")
public class PasswordController {

	private final PasswordManagementService passwordManagementService;

	private final PasswordResetService passwordResetService;

	@PostMapping(ApiPaths.CHANGE_PASSWORD_ENDPOINT)
	public ResponseWrapper<ChangePasswordRequestDto> changePassword(
			@Valid @RequestBody ChangePasswordRequestDto request,
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		passwordManagementService.changePassword(userDetails.getId(), request.getCurrentPassword(),
				request.getNewPassword(), request.getRepeatPassword());
		return ResponseFactory.ok(request);
	}

	@PostMapping(ApiPaths.RESET_PASSWORD_REQUEST_ENDPOINT)
	public ResponseEntity<ResponseWrapper<PasswordResetRequestDto>> requestReset(
			@Valid @RequestBody PasswordResetRequestDto request) {
		passwordResetService.sendResetCode(request.getEmail());

		return ResponseEntity.ok(ResponseFactory.ok(request));
	}

	@PostMapping(ApiPaths.RESET_PASSWORD_VERIFY_ENDPOINT)
	public ResponseEntity<ResponseWrapper<PasswordResetVerifyResponseDto>> verifyCode(
			@Valid @RequestBody PasswordResetVerifyDto request) {
		String resetToken = passwordResetService.verifyCode(request.getEmail(), request.getCode());

		return ResponseEntity.ok(ResponseFactory.ok(new PasswordResetVerifyResponseDto(resetToken)));
	}

	@PostMapping(ApiPaths.RESET_PASSWORD_CONFIRM_ENDPOINT)
	public ResponseWrapper<PasswordResetConfirmDto> confirmReset(@Valid @RequestBody PasswordResetConfirmDto request) {
		passwordManagementService.resetPassword(request.getResetToken(), request.getNewPassword(),
				request.getRepeatPassword());

		return ResponseFactory.ok(request);
	}

	@PostMapping(ApiPaths.SET_PASSWORD_ENDPOINT)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void setPassword(@RequestBody @Valid SetPasswordRequestDto request) {
		passwordManagementService.setPassword(request);
	}

}
