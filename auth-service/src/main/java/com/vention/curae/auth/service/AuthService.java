package com.vention.curae.auth.service;

import com.vention.curae.auth.dto.AuthRequestDto;
import com.vention.curae.auth.dto.AuthResponseDto;
import org.springframework.security.core.Authentication;

public interface AuthService {

	AuthResponseDto login(AuthRequestDto request);

	AuthResponseDto refresh(String refreshToken);

	void logout(String refreshToken);

	Authentication authenticateByAccessToken(String token);

}
