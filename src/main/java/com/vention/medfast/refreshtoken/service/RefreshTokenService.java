package com.vention.medfast.refreshtoken.service;

import com.vention.medfast.refreshtoken.model.RefreshToken;
import com.vention.medfast.user.model.User;

public interface RefreshTokenService {

	RefreshToken getByToken(String token);

	RefreshToken createToken(User user, String tokenValue);

	RefreshToken rotateToken(RefreshToken oldToken, String newTokenValue);

	void revokeRefreshToken(String tokenValue);

	void deleteAllExpired();

}
