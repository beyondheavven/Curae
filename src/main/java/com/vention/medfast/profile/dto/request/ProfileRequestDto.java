package com.vention.medfast.profile.dto.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRequestDto {

	@Valid
	private PersonalDetailsUpdateDto personalDetailsUpdateDto;

	@Valid
	private ProfileAddressUpdateDto profileAddressUpdateDto;

	@Valid
	private ContactsUpdateDto contactsUpdateDto;

}
