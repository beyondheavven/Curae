package com.vention.medfast.auth.jwt;

import com.vention.medfast.api.response.ErrorResponseDto;
import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.auth.jwt.service.JwtServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	public static final String BEARER_PREFIX = "Bearer ";

	private final JwtServiceImpl jwtServiceImpl;

	private final UserDetailsService userDetailsService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = authHeader.substring(BEARER_PREFIX.length());

		String username;
		try {
			username = jwtServiceImpl.extractUsername(token);
		}
		catch (Exception e) {
			writeUnauthorizedResponse(response, "Invalid JWT token");
			return;
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			try {
				CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

				if (jwtServiceImpl.isAccessToken(token) && jwtServiceImpl.isTokenValid(token, userDetails)) {

					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
							null, userDetails.getAuthorities());

					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);

				}
				else {
					writeUnauthorizedResponse(response, "Invalid or expired JWT token");
					return;
				}

			}
			catch (Exception e) {
				writeUnauthorizedResponse(response, "Invalid or expired JWT token");
				return;
			}
		}

		filterChain.doFilter(request, response);
	}

	private void writeUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        int status = HttpServletResponse.SC_UNAUTHORIZED;

        ErrorResponseDto error = ErrorResponseDto.builder().statusCode(status).message(message).build();

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        objectMapper.writeValue(response.getWriter(), error);
	}
}
