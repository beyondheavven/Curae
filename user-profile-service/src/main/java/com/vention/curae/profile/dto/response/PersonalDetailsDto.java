package com.vention.curae.profile.dto.response;

import com.vention.curae.patient.model.Gender;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
