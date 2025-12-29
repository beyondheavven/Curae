package com.vention.medfast.profile.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactsUpdateDto {

	@NotBlank
	@Email(message = "Invalid email format")
	@Size(max = 254)
	private String email;

	@NotBlank(message = "Phone number is required")
	private String phone;

}
