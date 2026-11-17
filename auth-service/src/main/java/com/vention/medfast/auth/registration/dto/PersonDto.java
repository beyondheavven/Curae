package com.vention.medfast.auth.registration.dto;

import com.vention.medfast.api.validation.annotation.ExistingCitizenship;
import com.vention.medfast.api.validation.annotation.ValidPassword;
import com.vention.medfast.patient.model.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonDto {

	@NotBlank
	@Size(max = 50)
	private String name;

	@NotBlank
	@Size(max = 50)
	private String lastName;

	@NotNull
	@Past(message = "Birth date must be in the past")
	private LocalDate birthDate;

	@NotNull
	private Gender gender;

	@NotBlank
	@Size(min = 8, message = "Password must be at least 8 characters long")
	@ValidPassword
	private String rawPassword;

	@NotBlank
	@Email(message = "Invalid email format")
	@Size(max = 254)
	private String email;

	@NotBlank
	@Size(max = 60)
	@ExistingCitizenship
	private String citizenship;

}
