package com.vention.medfast.passwordReset.service;

public interface PasswordManagementService {

	void changePassword(Long userId, String currentPassword, String newPassword, String repeatPassword);

	void resetPassword(String resetToken, String newPassword, String repeatPassword);

}
