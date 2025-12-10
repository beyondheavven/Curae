package com.vention.medfast.service;

import com.vention.medfast.dto.AuthRequest;
import com.vention.medfast.dto.AuthResponse;

public interface AuthService {

	public AuthResponse login(AuthRequest request);

	public AuthResponse refresh(String refreshToken);

	public void logout(String refreshToken);

}
