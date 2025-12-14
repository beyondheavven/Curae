package com.vention.medfast.chpassword.service;

import com.vention.medfast.user.model.User;
import com.vention.medfast.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class PasswordManagementServiceImpl implements PasswordManagementService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void changePassword(Long userId, String currentPassword, String newPassword, String repeatPassword) {
        User user = userService.findById(userId);
        validateCurrentPassword(currentPassword, user);
        validateNewPassword(newPassword, repeatPassword, user);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
    }

    @Override
    public void resetPassword(String email, String newPassword, String repeatPassword) {
        User user = userService.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        validateNewPassword(newPassword, repeatPassword, user);

        user.setPasswordHash(passwordEncoder.encode(newPassword));
    }

    private void validateCurrentPassword(String currentPassword, User user) {
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Incorrect current password. Please try again.");
        }
    }

    private void validateNewPassword(String newPassword, String repeatPassword, User user) {
        if (!newPassword.equals(repeatPassword)) {
            throw new IllegalArgumentException("New Password and Confirm New Password should be the same.");
        }
        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("The new password cannot be the same as the old one.");
        }
        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }
    }
}
