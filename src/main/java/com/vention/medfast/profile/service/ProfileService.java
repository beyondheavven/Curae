package com.vention.medfast.profile.service;

import com.vention.medfast.profile.dto.request.ProfileRequestDto;
import com.vention.medfast.profile.dto.response.ProfileResponseDto;

public interface ProfileService {

	ProfileResponseDto getProfile(Long userId);

	void updateProfile(Long userId, ProfileRequestDto profileRequestDto);

}
