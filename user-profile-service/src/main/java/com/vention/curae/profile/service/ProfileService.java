package com.vention.curae.profile.service;

import com.vention.curae.profile.dto.request.ProfileRequestDto;
import com.vention.curae.profile.dto.response.ProfileResponseDto;

public interface ProfileService {

	ProfileResponseDto getProfile(Long userId);

	void updateProfile(Long userId, ProfileRequestDto profileRequestDto);

}
