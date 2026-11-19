package com.vention.curae.refreshtoken.service;

import com.vention.curae.api.exception.InvalidRefreshTokenException;
import com.vention.curae.auth.jwt.JwtProperties;
import com.vention.curae.refreshtoken.model.RefreshToken;
import com.vention.curae.refreshtoken.repository.RefreshTokenRepository;
import com.vention.curae.user.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

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
			.orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));
	}

	@Override
	public RefreshToken createToken(User user) {
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setUsers(user);
		refreshToken.setToken(UUID.randomUUID().toString());
		refreshToken.setExpirationTime(Instant.now().plusMillis(jwtProperties.getRefreshTokenExpirationMs()));
		return refreshTokenRepository.save(refreshToken);
	}

	@Override
	public RefreshToken rotateToken(RefreshToken refreshToken) {
		refreshToken.setToken(UUID.randomUUID().toString());
		refreshToken.setExpirationTime(Instant.now().plusMillis(jwtProperties.getRefreshTokenExpirationMs()));
		return refreshToken;
	}

	@Override
	public void verifyExpiration(RefreshToken token) {
		if (token.getExpirationTime().compareTo(Instant.now()) < 0) {
			throw new InvalidRefreshTokenException("Refresh token expired");
		}
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
