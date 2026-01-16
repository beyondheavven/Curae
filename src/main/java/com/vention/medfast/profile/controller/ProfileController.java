package com.vention.medfast.profile.controller;

import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.profile.dto.request.ProfileRequestDto;
import com.vention.medfast.profile.dto.response.ProfileResponseDto;
import com.vention.medfast.profile.service.ProfileService;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Profile", description = "Endpoints for getting and updating users' profiles")
@RestController
@AllArgsConstructor
@RequestMapping(ApiPaths.PROFILE_ENDPOINT)
public class ProfileController {

	private final ProfileService profileService;

	@Operation(summary = "Get Profile", description = "Returns profile for authenticated user.")
	@GetMapping
	public ResponseWrapper<ProfileResponseDto> getProfile(@AuthenticationPrincipal CustomUserDetails user) {
		return ResponseWrapper.ok(profileService.getProfile(user.getId()));
	}

	@Operation(summary = "Update Profile", description = "Updates profile for authenticated user.")
	@PutMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(@AuthenticationPrincipal CustomUserDetails user,
			@Valid @RequestBody ProfileRequestDto profileRequestDto) {
		profileService.updateProfile(user.getId(), profileRequestDto);
	}

}
