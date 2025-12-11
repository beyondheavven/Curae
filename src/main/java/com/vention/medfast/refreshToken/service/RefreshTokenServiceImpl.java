package com.vention.medfast.refreshToken.service;

import com.vention.medfast.api.exception.InvalidRefreshTokenException;
import com.vention.medfast.refreshToken.model.RefreshToken;
import com.vention.medfast.refreshToken.model.RefreshTokenStatus;
import com.vention.medfast.refreshToken.repository.RefreshTokenRepository;
import com.vention.medfast.user.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

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
		refreshToken.setStatus(RefreshTokenStatus.VALID);
		refreshToken.setUsers(user);
		return refreshTokenRepository.save(refreshToken);
	}

	@Override
	public RefreshToken rotateToken(RefreshToken oldToken, String newTokenValue) {
		oldToken.setStatus(RefreshTokenStatus.REVOKED);
		refreshTokenRepository.save(oldToken);

		RefreshToken newRefresh = new RefreshToken();
		newRefresh.setToken(newTokenValue);
		newRefresh.setUsers(oldToken.getUsers());
		newRefresh.setStatus(RefreshTokenStatus.VALID);

		return refreshTokenRepository.save(newRefresh);
	}

	@Override
	public void revokeRefreshToken(String tokenValue) {
		RefreshToken token = refreshTokenRepository.findByToken(tokenValue)
			.orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not found"));
		if (token.getStatus() == RefreshTokenStatus.REVOKED) {
			return;
		}

		token.setStatus(RefreshTokenStatus.REVOKED);
		refreshTokenRepository.save(token);
	}

}
