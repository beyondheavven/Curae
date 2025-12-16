package com.vention.medfast.passwordReset.service;

import com.vention.medfast.mail.model.EmailToken;
import com.vention.medfast.mail.model.EmailTokenStatus;
import com.vention.medfast.mail.repository.EmailTokenRepository;
import com.vention.medfast.mail.token.EmailTokenGenerator;
import com.vention.medfast.passwordReset.validation.PasswordChangeValidator;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@RequiredArgsConstructor
@Service
@Transactional
public class PasswordManagementServiceImpl implements PasswordManagementService {

    private final UserService userService;
    private final EmailTokenRepository emailTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordChangeValidator validator;

    @Override
    public void changePassword(Long userId, String currentPassword, String newPassword, String repeatPassword) {
        User user = userService.findById(userId);
        validator.validateChange(user, currentPassword, newPassword, repeatPassword);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
    }

    @Override
    public void resetPassword(String resetToken, String newPassword, String repeatPassword) {
        String tokenHash = EmailTokenGenerator.hash(resetToken);
        EmailToken token = emailTokenRepository.findByTokenHash(tokenHash)
            .orElseThrow(() -> new IllegalArgumentException("Invalid reset token"));
        if (token.getStatus() != EmailTokenStatus.VERIFIED) {
            throw new IllegalArgumentException("Reset token is not verified");
        }
        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Reset token expired");
        }
        User user = token.getUser();
        validator.validateReset(user, newPassword, repeatPassword);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
    }
}
