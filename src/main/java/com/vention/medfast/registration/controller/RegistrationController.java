package com.vention.medfast.registration.controller;

import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.registration.dto.RegistrationRequestDto;
import com.vention.medfast.registration.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(ApiPaths.REGISTRATION_ENDPOINT)
public class RegistrationController {

	private final RegistrationService registrationService;

	@PostMapping
	@Operation(summary = "Register new user",
			description = "Registers a new user with personal and address information",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
					content = @Content(schema = @Schema(implementation = RegistrationRequestDto.class),
							examples = @ExampleObject(name = "Registration request", value = """
									{
									  {
									    "person": {
									      "name": "John",
									      "lastName": "Doe",
									      "birthDate": "1990-05-15",
									      "gender": "MALE",
									      "rawPassword": "StrongPass123!",
									      "email": "johaanf.doe@example.com",
									      "phoneNumber": "+375291234567",
									      "citizenship": "Belarusian"
									    },
									    "address": {
									      "street": "Lenina",
									      "city": "Minsk",
									      "apartment": "12",
									      "houseNumber": "8",
									      "state": "Belarus",
									      "zipCode": "220030",
									      "phoneNumber": "+375291234567"
									    }
									  }
									"""))))
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "User successfully registered"),
			@ApiResponse(responseCode = "400", description = "Validation error"),
			@ApiResponse(responseCode = "409", description = "User with this email already exists") })
	@ResponseStatus(HttpStatus.CREATED)
	public void register(@RequestBody @Valid RegistrationRequestDto request) {
		registrationService.register(request);
	}

}
