package com.vention.medfast.service;

import com.vention.medfast.model.*;
import com.vention.medfast.repository.*;
import lombok.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class PasswordResetService {

    private final PasswordResetCodeRepository repo;
    private final UserRepository userRepo;
    private final EmailService emailService;

    public void sendResetCode(String email) {

        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String code = generateCode();

        PasswordResetCode reset = new PasswordResetCode();
        reset.setUser(user);
        reset.setCode(code);
        reset.setExpiresAt(LocalDateTime.now().plusMinutes(4));

        repo.save(reset);

        emailService.sendPasswordResetCode(user.getEmail(), code);
    }

    public boolean verifyCode(String email, String code) {
        PasswordResetCode reset = repo.findTopByUserEmailAndUsedIsFalseOrderByExpiresAtDesc(email)
            .orElseThrow(() -> new IllegalArgumentException("No reset code found"));

        if (reset.isUsed()) return false;
        if (reset.getExpiresAt().isBefore(LocalDateTime.now())) return false;

        if (!reset.getCode().equals(code)) return false;

        reset.setUsed(true);
        repo.save(reset);

        return true;
    }

    private String generateCode() {
        return String.format("%04d", new Random().nextInt(10000));
    }
}
