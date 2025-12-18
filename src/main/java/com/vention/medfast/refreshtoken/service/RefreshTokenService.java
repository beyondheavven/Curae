package com.vention.medfast.refreshtoken.service;

import com.vention.medfast.refreshtoken.model.RefreshToken;
import com.vention.medfast.user.model.User;

public interface RefreshTokenService {

	public RefreshToken getByToken(String token);

	public RefreshToken createToken(User user, String tokenValue);

	public RefreshToken rotateToken(RefreshToken oldToken, String newTokenValue);

	public void revokeRefreshToken(String tokenValue);

}
