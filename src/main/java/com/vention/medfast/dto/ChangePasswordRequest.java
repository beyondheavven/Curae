package com.vention.medfast.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class ChangePasswordRequest {
    @NotBlank
    private String currentPassword;
    @NotBlank
    private String newPassword;
    @NotBlank
    private String repeatPassword;
}
