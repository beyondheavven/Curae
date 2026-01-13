package com.vention.medfast.profile.dto.response;

import com.vention.medfast.patient.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonalDetailsDto {

	private String name;

	private String lastName;

	private LocalDate birthDate;

	private Gender gender;

	private String citizenship;

	private String photoUrl;

}
