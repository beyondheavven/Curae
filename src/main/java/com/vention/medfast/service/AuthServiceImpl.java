package com.vention.medfast.service;

import com.vention.medfast.api.exception.InvalidRefreshTokenException;
import com.vention.medfast.dto.AuthRequest;
import com.vention.medfast.dto.AuthResponse;
import com.vention.medfast.model.RefreshToken;
import com.vention.medfast.model.RefreshTokenStatus;
import com.vention.medfast.model.User;
import com.vention.medfast.security.core.CustomUserDetails;
import com.vention.medfast.security.jwt.JwtService;
import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final AuthenticationManager authenticationManager;

	private final JwtService jwtService;

	private final UserDetailsService userDetailsService;

	private final UserService userService;

	private final RefreshTokenService refreshTokenService;

	@Override
	public AuthResponse login(AuthRequest request) {
		try {
			Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

			CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();

			String accessToken = jwtService.generateAccessToken(customUser);
			String refreshToken = jwtService.generateRefreshToken(customUser);

			User user = userService.findById(customUser.getId());

			refreshTokenService.createToken(user, refreshToken);

			return new AuthResponse(accessToken, refreshToken);
		}
		catch (BadCredentialsException ex) {
			throw new BadCredentialsException("Invalid email or password");
		}
	}

	@Override
	public AuthResponse refresh(String refreshToken) {
		try {
			if (!jwtService.isRefreshToken(refreshToken)) {
				throw new InvalidRefreshTokenException("Token is not a refresh token");
			}

			String username = jwtService.extractUsername(refreshToken);
			CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

			if (!jwtService.isTokenValid(refreshToken, userDetails)) {
				throw new InvalidRefreshTokenException("Refresh token is invalid");
			}

			RefreshToken stored = refreshTokenService.getByToken(refreshToken);
			if (stored.getStatus() != RefreshTokenStatus.VALID) {
				throw new InvalidRefreshTokenException("Refresh token is no longer valid");
			}

			String newRefresh = jwtService.generateRefreshToken(userDetails);
			refreshTokenService.rotateToken(stored, newRefresh);
			String newAccess = jwtService.generateAccessToken(userDetails);

			return new AuthResponse(newAccess, newRefresh);

		}
		catch (JwtException | IllegalArgumentException e) {
			throw new InvalidRefreshTokenException("Invalid refresh token");
		}
	}

	@Override
	public void logout(String refreshToken) {
		String username = jwtService.extractUsername(refreshToken);
		CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

		if (!jwtService.isRefreshToken(refreshToken) || !jwtService.isTokenValid(refreshToken, userDetails)) {
			throw new InvalidRefreshTokenException("Invalid refresh token");
		}

		refreshTokenService.revokeRefreshToken(refreshToken);
	}

}
