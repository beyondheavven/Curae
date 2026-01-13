package com.vention.medfast.auth.jwt.service;

import com.vention.medfast.auth.core.CustomUserDetails;
import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.function.Function;

public interface JwtService {

	String generateAccessToken(CustomUserDetails user);

	String generateRefreshToken(CustomUserDetails user);

	<T> T extractClaim(String token, Function<Claims, T> resolver);

	String extractUsername(String token);

	Date extractExpiration(String token);

	boolean isTokenExpired(String token);

	boolean isAccessToken(String token);

	boolean isRefreshToken(String token);

	boolean isTokenValid(String token, CustomUserDetails userDetails);

}
