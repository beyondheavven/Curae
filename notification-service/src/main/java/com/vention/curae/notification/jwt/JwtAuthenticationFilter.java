package com.vention.curae.notification.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authorization == null || !authorization.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			Claims claims = Jwts.parser()
				.verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
				.build()
				.parseSignedClaims(authorization.substring(7))
				.getPayload();

			String subject = claims.getSubject();
			List<SimpleGrantedAuthority> authorities = java.util.Collections.emptyList();
			Object role = claims.get("role");
			if (role != null) {
				String authority = role.toString().toUpperCase(Locale.ROOT);
				if (!authority.startsWith("ROLE_")) {
					authority = "ROLE_" + authority;
				}
				authorities = List.of(new SimpleGrantedAuthority(authority));
			}

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(subject,
					authorization, authorities);
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		catch (Exception ex) {
			SecurityContextHolder.clearContext();
		}

		filterChain.doFilter(request, response);
	}

}
