package com.vention.medfast.controller;

import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.dto.*;
import com.vention.medfast.security.core.CustomUserDetails;
import com.vention.medfast.service.*;
import com.vention.medfast.wrapper.*;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RequestMapping(ApiPaths.ACCOUNT_ENDPOINT)
@AllArgsConstructor
@Tag(name = "Account", description = "Endpoints for account management")
@RestController
public class ChangePasswordController {

    private final PasswordManagementService passwordService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/change-password")
    public ResponseWrapper<String> changePassword(
        @Valid @RequestBody ChangePasswordRequest request,
        Authentication auth
    ) {
        Long userId = ((CustomUserDetails) auth.getPrincipal()).getId();
        try {
            passwordService.changePassword(
                userId,
                request.getCurrentPassword(),
                request.getNewPassword(),
                request.getRepeatPassword()
            );
            return ResponseFactory.ok("Password has been successfully changed");
        }
        catch (IllegalArgumentException ex) {
            return ResponseFactory.badRequest(
                ex.getMessage(),
                ErrorCode.BAD_REQUEST
            );
        }
    }

    @PostMapping("/password-reset/request")
    public ResponseWrapper<String> requestReset(@RequestBody PasswordResetRequestDto request) {
        try {
            passwordResetService.sendResetCode(request.getEmail());
            return ResponseFactory.ok("Reset code sent to email");
        }
        catch (IllegalArgumentException ex) {
            return ResponseFactory.badRequest(
                ex.getMessage(),
                ErrorCode.BAD_REQUEST
            );
        }
    }

    @PostMapping("/password-reset/verify")
    public ResponseWrapper<String> verifyCode(@RequestBody PasswordResetVerifyDto request) {
        boolean ok = passwordResetService.verifyCode(request.getEmail(), request.getCode());
        if (!ok) {
            return ResponseFactory.badRequest(
                "Code is wrong",
                ErrorCode.VALIDATION_ERROR
            );
        }
        return ResponseFactory.ok("Code verified");
    }

    @PostMapping("/password-reset/confirm")
    public ResponseWrapper<String> confirmReset(@RequestBody PasswordResetConfirmDto request) {
        try {
            passwordService.resetPassword(
                request.getEmail(),
                request.getNewPassword(),
                request.getRepeatPassword()
            );
            return ResponseFactory.ok("Password has been successfully reset");
        } catch (IllegalArgumentException ex) {
            return ResponseFactory.badRequest(
                ex.getMessage(),
                ErrorCode.BAD_REQUEST
            );
        }
    }
}
