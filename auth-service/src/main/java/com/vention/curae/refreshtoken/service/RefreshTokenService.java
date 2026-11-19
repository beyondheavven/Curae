package com.vention.curae.refreshtoken.service;

import com.vention.curae.refreshtoken.model.RefreshToken;
import com.vention.curae.user.model.User;

public interface RefreshTokenService {

	RefreshToken getByToken(String token);

	RefreshToken createToken(User user);

	RefreshToken rotateToken(RefreshToken refreshToken);

	void verifyExpiration(RefreshToken token);

	void revokeRefreshToken(String tokenValue);

	void deleteAllExpired();

}
