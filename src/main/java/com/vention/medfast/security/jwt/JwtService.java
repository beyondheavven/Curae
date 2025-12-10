package com.vention.medfast.security.jwt;

import com.vention.medfast.security.core.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

	public static final String ACCESS = "access";

	public static final String REFRESH = "refresh";

	public static final String CLAIM_UID = "uid";

	public static final String CLAIM_ROLE = "role";

	public static final String CLAIM_TYPE = "type";

	public static final String HEADER_TYPE = "JWT";

	private final JwtProperties jwtProperties;

	private final SecretKey signingKey;

	public JwtService(JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
		this.signingKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
	}

	public String generateAccessToken(CustomUserDetails user) {
		Map<String, Object> claims = buildBaseClaims(user);
		return buildToken(claims, user.getUsername(), jwtProperties.getAccessTokenExpirationMs(), ACCESS);
	}

	public String generateRefreshToken(CustomUserDetails user) {
		Map<String, Object> claims = buildBaseClaims(user);
		return buildToken(claims, user.getUsername(), jwtProperties.getRefreshTokenExpirationMs(), REFRESH);
	}

	private Map<String, Object> buildBaseClaims(CustomUserDetails user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(CLAIM_UID, user.getId());
		claims.put(CLAIM_ROLE, user.getRoleName());
		return claims;
	}

	private String buildToken(Map<String, Object> extraClaims, String subject, long expirationMs, String tokenType) {

		Instant now = Instant.now();
		Instant expiry = now.plusMillis(expirationMs);
		extraClaims.put(CLAIM_TYPE, tokenType);

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

	public <T> T extractClaim(String token, Function<Claims, T> resolver) {
		Claims claims = parseToken(token).getPayload();
		return resolver.apply(claims);
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public boolean isAccessToken(String token) {
		String type = extractClaim(token, c -> c.get(CLAIM_TYPE, String.class));
		return ACCESS.equals(type);
	}

	public boolean isRefreshToken(String token) {
		String type = extractClaim(token, c -> c.get(CLAIM_TYPE, String.class));
		return REFRESH.equals(type);
	}

	public boolean isTokenValid(String token, CustomUserDetails userDetails) {
		String usernameFromToken = extractUsername(token);
		Long idFromToken = extractClaim(token, c -> c.get(CLAIM_UID, Long.class));
		String roleFromToken = extractClaim(token, c -> c.get(CLAIM_ROLE, String.class));

		boolean notExpired = !isTokenExpired(token);
		boolean usernameMatches = usernameFromToken.equals(userDetails.getUsername());
		boolean idMatches = idFromToken.equals(userDetails.getId());
		boolean roleMatches = roleFromToken.equals(userDetails.getRoleName());

		return notExpired && usernameMatches && idMatches && roleMatches;
	}

}
