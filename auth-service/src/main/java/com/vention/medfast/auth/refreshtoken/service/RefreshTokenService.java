package com.vention.medfast.auth.refreshtoken.service;

import com.vention.medfast.refreshtoken.model.RefreshToken;
import com.vention.medfast.user.model.User;

public interface RefreshTokenService {

	RefreshToken getByToken(String token);

	RefreshToken createToken(User user);

	RefreshToken rotateToken(RefreshToken refreshToken);

	void verifyExpiration(RefreshToken token);

	void revokeRefreshToken(String tokenValue);

	void deleteAllExpired();

}
