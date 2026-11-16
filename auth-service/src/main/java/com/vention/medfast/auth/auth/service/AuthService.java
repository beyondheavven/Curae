package com.vention.medfast.auth.auth.service;

import com.vention.medfast.auth.dto.AuthRequestDto;
import com.vention.medfast.auth.dto.AuthResponseDto;
import org.springframework.security.core.Authentication;

public interface AuthService {

	AuthResponseDto login(AuthRequestDto request);

	AuthResponseDto refresh(String refreshToken);

	void logout(String refreshToken);

	Authentication authenticateByAccessToken(String token);

}
