package com.vention.medfast.auth.auth.jwt.service;

import com.vention.medfast.auth.auth.core.CustomUserDetails;
import com.vention.medfast.auth.auth.jwt.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

	public static final String ACCESS = "access";

	public static final String CLAIM_UID = "uid";

	public static final String CLAIM_ROLE = "role";

	public static final String CLAIM_TYPE = "type";

	public static final String HEADER_TYPE = "JWT";

	private final JwtProperties jwtProperties;

	private final SecretKey signingKey;

	public JwtServiceImpl(JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
		this.signingKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String generateAccessToken(CustomUserDetails user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(CLAIM_UID, user.getId());
		claims.put(CLAIM_ROLE, user.getRoleName());
		return buildAccessToken(claims, user.getUsername(), jwtProperties.getAccessTokenExpirationMs());
	}

	private String buildAccessToken(Map<String, Object> extraClaims, String subject, long expirationMs) {
		Instant now = Instant.now();
		Instant expiry = now.plusMillis(expirationMs);

		extraClaims.put(CLAIM_TYPE, ACCESS);

		return Jwts.builder()
			.header()
			.type(HEADER_TYPE)
			.and()
			.claims(extraClaims)
			.subject(subject)
			.issuedAt(Date.from(now))
			.expiration(Date.from(expiry))
			.signWith(signingKey)
			.compact();
	}

	private Jws<Claims> parseToken(String token) {
		return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token);
	}

	private Claims extractAllClaims(String token) {
		return parseToken(token).getPayload();
	}

	@Override
	public <T> T extractClaim(String token, Function<Claims, T> resolver) {
		Claims claims = extractAllClaims(token);
		return resolver.apply(claims);
	}

	@Override
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Long extractUserId(String token) {
		Number uid = extractClaim(token, c -> c.get(CLAIM_UID, Number.class));
		return uid == null ? null : uid.longValue();
	}

	@Override
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	@Override
	public boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	@Override
	public boolean isAccessToken(String token) {
		String type = extractClaim(token, c -> c.get(CLAIM_TYPE, String.class));
		return ACCESS.equals(type);
	}

	@Override
	public boolean isTokenValid(String token, CustomUserDetails userDetails) {
		Long idFromToken = extractUserId(token);
		String roleFromToken = extractClaim(token, c -> c.get(CLAIM_ROLE, String.class));

		boolean notExpired = !isTokenExpired(token);
		boolean idMatches = idFromToken != null && idFromToken.equals(userDetails.getId());
		boolean roleMatches = roleFromToken != null && roleFromToken.equals(userDetails.getRoleName());

		return notExpired && idMatches && roleMatches;
	}

}
