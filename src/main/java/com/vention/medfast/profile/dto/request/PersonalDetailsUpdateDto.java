package com.vention.medfast.profile.dto.request;

import com.vention.medfast.api.validation.annotation.ExistingCitizenship;
import com.vention.medfast.patient.model.Gender;
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
public class PersonalDetailsUpdateDto {

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
	@Size(max = 60)
	@ExistingCitizenship
	private String citizenship;

	private String photoUrl;

}
