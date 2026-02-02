package com.vention.medfast.refreshtoken.service;

import com.vention.medfast.api.exception.InvalidRefreshTokenException;
import com.vention.medfast.auth.jwt.JwtProperties;
import com.vention.medfast.refreshtoken.model.RefreshToken;
import com.vention.medfast.refreshtoken.repository.RefreshTokenRepository;
import com.vention.medfast.user.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@AllArgsConstructor
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

	private final JwtProperties jwtProperties;

	private final RefreshTokenRepository refreshTokenRepository;

	@Override
	@Transactional(readOnly = true)
	public RefreshToken getByToken(String token) {
		return refreshTokenRepository.findByToken(token)
			.orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not found"));
	}

	@Override
	public RefreshToken createToken(User user, String tokenValue) {
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setToken(tokenValue);
		refreshToken.setExpirationTime(Instant.now().plusMillis(jwtProperties.getRefreshTokenExpirationMs()));
		refreshToken.setUsers(user);
		return refreshTokenRepository.save(refreshToken);
	}

	@Override
	public RefreshToken rotateToken(RefreshToken oldToken, String newTokenValue) {
		RefreshToken refreshToken = getByToken(oldToken.getToken());
		refreshToken.setToken(newTokenValue);
		refreshToken.setExpirationTime(Instant.now().plusMillis(jwtProperties.getRefreshTokenExpirationMs()));
		return refreshToken;
	}

	@Override
	public void revokeRefreshToken(String tokenValue) {
		RefreshToken token = getByToken(tokenValue);
		refreshTokenRepository.delete(token);
	}

	@Transactional
	@Override
	public void deleteAllExpired() {
		refreshTokenRepository.deleteAll(refreshTokenRepository.findAllExpired(Instant.now()));
	}

}
