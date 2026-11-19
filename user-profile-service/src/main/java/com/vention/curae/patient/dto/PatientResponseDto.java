package com.vention.curae.patient.dto;

import com.vention.curae.patient.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponseDto {

	private Long id;

	private Long userId;

	private String citizenship;

	private Gender gender;

}
