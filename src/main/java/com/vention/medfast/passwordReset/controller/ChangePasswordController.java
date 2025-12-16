package com.vention.medfast.passwordReset.controller;

import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.passwordReset.dto.*;
import com.vention.medfast.passwordReset.service.PasswordManagementService;
import com.vention.medfast.passwordReset.service.PasswordResetService;
import com.vention.medfast.wrapper.ResponseFactory;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.ACCOUNT_ENDPOINT)
@AllArgsConstructor
@Tag(name = "Account", description = "Endpoints for account management")
public class ChangePasswordController {

    private final PasswordManagementService passwordService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/change-password")
    public ResponseWrapper<String> changePassword(
        @Valid @RequestBody ChangePasswordRequestDto request,
        Authentication auth
    ) {
        Long userId = ((CustomUserDetails) auth.getPrincipal()).getId();
        passwordService.changePassword(
            userId,
            request.getCurrentPassword(),
            request.getNewPassword(),
            request.getRepeatPassword()
        );
        return ResponseFactory.ok("Password has been successfully changed");
    }

    @PostMapping("/password-reset/request")
    public ResponseWrapper<String> requestReset(
        @Valid @RequestBody PasswordResetRequestDto request
    ) {
        passwordResetService.sendResetCode(request.getEmail());
        return ResponseFactory.ok("Reset code sent to email");
    }

    @PostMapping("/password-reset/verify")
    public ResponseWrapper<PasswordResetVerifyResponseDto> verifyCode(
        @Valid @RequestBody PasswordResetVerifyDto request
    ) {
        String resetToken = passwordResetService.verifyCode(
            request.getEmail(),
            request.getCode()
        );
        return ResponseFactory.ok(
            new PasswordResetVerifyResponseDto(resetToken)
        );
    }

    @PostMapping("/password-reset/confirm")
    public ResponseWrapper<String> confirmReset(
        @Valid @RequestBody PasswordResetConfirmDto request
    ) {
        passwordService.resetPassword(
            request.getResetToken(),
            request.getNewPassword(),
            request.getRepeatPassword()
        );
        return ResponseFactory.ok("Password has been successfully reset");
    }
}
