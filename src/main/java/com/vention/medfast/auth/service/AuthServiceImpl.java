package com.vention.medfast.auth.service;

import com.vention.medfast.api.exception.InvalidRefreshTokenException;
import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.auth.dto.AuthRequestDto;
import com.vention.medfast.auth.dto.AuthResponseDto;
import com.vention.medfast.auth.jwt.service.JwtService;
import com.vention.medfast.refreshtoken.model.RefreshToken;
import com.vention.medfast.refreshtoken.service.RefreshTokenService;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.service.UserService;
import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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
	public AuthResponseDto login(AuthRequestDto request) {
		try {
			Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

			CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();

			String accessToken = jwtService.generateAccessToken(customUser);
			String refreshToken = jwtService.generateRefreshToken(customUser);

			User user = userService.findById(customUser.getId());

			refreshTokenService.createToken(user, refreshToken);

			return new AuthResponseDto(accessToken, refreshToken);
		}
		catch (BadCredentialsException ex) {
			throw new BadCredentialsException("Invalid email or password");
		}
	}

	@Override
	public AuthResponseDto refresh(String refreshToken) {
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

			String newRefresh = jwtService.generateRefreshToken(userDetails);
			refreshTokenService.rotateToken(stored, newRefresh);
			String newAccess = jwtService.generateAccessToken(userDetails);

			return new AuthResponseDto(newAccess, newRefresh);

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

	@Override
	public Authentication authenticateByAccessToken(String token) {
		if (!jwtService.isAccessToken(token)) {
			throw new AccessDeniedException("Invalid access token");
		}

		String username = jwtService.extractUsername(token);
		if (username == null) {
			throw new AccessDeniedException("Invalid JWT token");
		}

		CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

		if (!jwtService.isTokenValid(token, userDetails)) {
			throw new AccessDeniedException("Invalid or expired JWT token");
		}

		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

}
