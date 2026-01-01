package com.vention.medfast.profile.controller;

import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.profile.dto.request.ProfileRequestDto;
import com.vention.medfast.profile.dto.response.ProfileResponseDto;
import com.vention.medfast.profile.service.ProfileService;
import com.vention.medfast.wrapper.ResponseFactory;
import com.vention.medfast.wrapper.ResponseWrapper;
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

@RestController
@AllArgsConstructor
@RequestMapping(ApiPaths.PROFILE_ENDPOINT)
public class ProfileController {

	private final ProfileService profileService;

	@GetMapping
	public ResponseWrapper<ProfileResponseDto> getProfile(@AuthenticationPrincipal CustomUserDetails user) {

		return ResponseFactory.ok(profileService.getProfile(user.getId()));
	}

	@PutMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateProfile(@AuthenticationPrincipal CustomUserDetails user,
			@Valid @RequestBody ProfileRequestDto profileRequestDto) {
		profileService.updateProfile(user.getId(), profileRequestDto);
	}

}
