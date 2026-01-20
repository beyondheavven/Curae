package com.vention.medfast.auth.jwt;

import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.auth.jwt.service.JwtServiceImpl;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	public static final String BEARER_PREFIX = "Bearer ";

	private final JwtServiceImpl jwtServiceImpl;

	private final UserRepository userRepository;

	private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = authHeader.substring(BEARER_PREFIX.length()).trim();
		if (token.isEmpty()) {
			unauthorized(request, response, "Invalid JWT token");
			return;
		}

		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			filterChain.doFilter(request, response);
			return;
		}

		Long uid;
		try {
			uid = jwtServiceImpl.extractUserId(token);
		}
		catch (Exception e) {
			unauthorized(request, response, "Invalid JWT token");
			return;
		}

		if (uid == null) {
			unauthorized(request, response, "Invalid JWT token");
			return;
		}

		try {
			User user = userRepository.findById(uid).orElseThrow(() -> new UsernameNotFoundException("User not found"));

			CustomUserDetails userDetails = new CustomUserDetails(user.getId(), user.getEmail(), user.getPasswordHash(),
					user.getRole());

			if (jwtServiceImpl.isAccessToken(token) && jwtServiceImpl.isTokenValid(token, userDetails)) {
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());

				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);

				filterChain.doFilter(request, response);
				return;
			}

			unauthorized(request, response, "Invalid or expired JWT token");
		}
		catch (Exception e) {
			unauthorized(request, response, "Invalid or expired JWT token");
		}
	}

	private void unauthorized(HttpServletRequest request, HttpServletResponse response, String message)
			throws IOException {

		restAuthenticationEntryPoint.commence(request, response, new AuthenticationException(message) {
		});
	}

}
