package com.vention.medfast.passwordReset.validation;

import com.vention.medfast.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PasswordChangeValidator {

    private final PasswordEncoder passwordEncoder;

    // ===== CHANGE PASSWORD =====
    public void validateChange(
        User user,
        String currentPassword,
        String newPassword,
        String repeatPassword
    ) {
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Incorrect current password. Please try again.");
        }

        if (!newPassword.equals(repeatPassword)) {
            throw new IllegalArgumentException("New Password and Confirm New Password should be the same.");
        }

        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("The new password cannot be the same as the old one.");
        }
    }

    // ===== RESET PASSWORD =====
    public void validateReset(
        User user,
        String newPassword,
        String repeatPassword
    ) {
        if (!newPassword.equals(repeatPassword)) {
            throw new IllegalArgumentException("New Password and Confirm New Password should be the same.");
        }

        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("The new password cannot be the same as the old one.");
        }
    }
}
