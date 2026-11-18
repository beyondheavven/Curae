package com.vention.medfast.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponseDto {

	private PersonalDetailsDto personalDetails;

	private ProfileAddressDto profileAddress;

	private ContactsDto contactsDto;

	private String status;

}
