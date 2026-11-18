package com.vention.medfast.profile.controller;

import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.profile.dto.request.ProfileRequestDto;
import com.vention.medfast.profile.dto.response.ProfileResponseDto;
import com.vention.medfast.profile.service.ProfileService;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Profile", description = "Endpoints for getting and updating users' profiles")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.PROFILE_ENDPOINT)
public class ProfileController {

	private final ProfileService profileService;

	@Operation(summary = "Get Profile", description = "Returns profile for authenticated user.")
	@GetMapping
	public ResponseWrapper<ProfileResponseDto> getProfile(Authentication authentication) {
		Long userId = Long.valueOf(authentication.getName());
		return ResponseWrapper.ok(profileService.getProfile(userId));
	}

	@Operation(summary = "Update Profile", description = "Updates profile for authenticated user.")
	@PutMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(Authentication authentication, @Valid @RequestBody ProfileRequestDto profileRequestDto) {
		Long userId = Long.valueOf(authentication.getName());
		profileService.updateProfile(userId, profileRequestDto);
	}

}
