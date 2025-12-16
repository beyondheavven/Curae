package com.vention.medfast.passwordReset.controller;

import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.passwordReset.dto.ChangePasswordRequestDto;
import com.vention.medfast.passwordReset.dto.PasswordResetRequestDto;
import com.vention.medfast.passwordReset.dto.PasswordResetVerifyDto;
import com.vention.medfast.passwordReset.dto.PasswordResetVerifyResponseDto;
import com.vention.medfast.passwordReset.dto.PasswordResetConfirmDto;
import com.vention.medfast.passwordReset.service.PasswordManagementService;
import com.vention.medfast.passwordReset.service.PasswordResetService;
import com.vention.medfast.wrapper.ResponseFactory;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ResponseWrapper<ChangePasswordRequestDto>> changePassword(
        @Valid @RequestBody ChangePasswordRequestDto request,
        Authentication auth
    ) {
        Long userId =
            ((CustomUserDetails) auth.getPrincipal()).getId();
        passwordService.changePassword(
            userId,
            request.getCurrentPassword(),
            request.getNewPassword(),
            request.getRepeatPassword()
        );
        return ResponseEntity.ok(
            ResponseFactory.ok(request)
        );
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<ResponseWrapper<PasswordResetRequestDto>> requestReset(
        @Valid @RequestBody PasswordResetRequestDto request
    ) {
        passwordResetService.sendResetCode(request.getEmail());

        return ResponseEntity.ok(
            ResponseFactory.ok(request)
        );
    }

    @PostMapping("/password-reset/verify")
    public ResponseEntity<ResponseWrapper<PasswordResetVerifyResponseDto>> verifyCode(
        @Valid @RequestBody PasswordResetVerifyDto request
    ) {
        String resetToken = passwordResetService.verifyCode(
            request.getEmail(),
            request.getCode()
        );

        return ResponseEntity.ok(
            ResponseFactory.ok(
                new PasswordResetVerifyResponseDto(resetToken)
            )
        );
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<ResponseWrapper<PasswordResetConfirmDto>> confirmReset(
        @Valid @RequestBody PasswordResetConfirmDto request
    ) {
        passwordService.resetPassword(
            request.getResetToken(),
            request.getNewPassword(),
            request.getRepeatPassword()
        );

        return ResponseEntity.ok(
            ResponseFactory.ok(request)
        );
    }
}
