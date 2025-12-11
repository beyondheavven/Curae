package com.vention.medfast.auth.service;

import com.vention.medfast.auth.dto.AuthRequestDto;
import com.vention.medfast.auth.dto.AuthResponseDto;

public interface AuthService {

	AuthResponseDto login(AuthRequestDto request);

	AuthResponseDto refresh(String refreshToken);

	void logout(String refreshToken);

}
