package com.vention.medfast.service;

public interface PasswordManagementService {
    void changePassword(Long userId, String currentPassword, String newPassword, String repeatPassword);
    void resetPassword(String email, String newPassword, String repeatPassword);
}
