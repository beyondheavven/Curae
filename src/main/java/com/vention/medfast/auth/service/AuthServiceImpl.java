package com.vention.medfast.auth.service;

import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.auth.core.CustomUserDetailsService;
import com.vention.medfast.auth.dto.AuthRequestDto;
import com.vention.medfast.auth.dto.AuthResponseDto;
import com.vention.medfast.auth.jwt.service.JwtService;
import com.vention.medfast.refreshtoken.model.RefreshToken;
import com.vention.medfast.refreshtoken.service.RefreshTokenService;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final AuthenticationManager authenticationManager;

	private final JwtService jwtService;

	private final CustomUserDetailsService userDetailsService;

	private final UserService userService;

	private final RefreshTokenService refreshTokenService;

	@Override
	public AuthResponseDto login(AuthRequestDto request) {
		Authentication authentication = authenticationManager
			.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

		CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();
		String accessToken = jwtService.generateAccessToken(customUser);

		User user = userService.findById(customUser.getId());
		RefreshToken refreshToken = refreshTokenService.createToken(user);

		return new AuthResponseDto(accessToken, refreshToken.getToken());
	}

	@Override
	public AuthResponseDto refresh(String refreshToken) {
		RefreshToken token = refreshTokenService.getByToken(refreshToken);
		refreshTokenService.verifyExpiration(token);

		CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserEntity(token.getUsers());

		String newAccess = jwtService.generateAccessToken(userDetails);
		RefreshToken newToken = refreshTokenService.rotateToken(token);
		return new AuthResponseDto(newAccess, newToken.getToken());
	}

	@Override
	public void logout(String refreshToken) {
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
