package com.vention.medfast.passwordReset.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class ChangePasswordRequestDto {
    @NotBlank
    private String currentPassword;
    @NotBlank
    @Size(min = 8)
    private String newPassword;
    @NotBlank
    private String repeatPassword;
}
