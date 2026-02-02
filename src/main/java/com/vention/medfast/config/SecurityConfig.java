package com.vention.medfast.config;

import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.auth.jwt.JwtAuthenticationFilter;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Value("${custom.security.public-routes}")
	private String[] actuatorEndpoints;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService(UserRepository userRepository) {
		return email -> userRepository.findByEmail(email)
			.map(user -> new CustomUserDetails(user.getId(), user.getEmail(), user.getPasswordHash(), user.getRole()))
			.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

	@Bean
	public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter,
			AuthenticationProvider authenticationProvider) throws Exception {

		http.csrf(csrf -> csrf.disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**")
				.permitAll()
				.requestMatchers(HttpMethod.POST, "/registration")
				.permitAll()

				.requestMatchers("/auth/login", "/auth/refresh", "/auth/verify")
				.permitAll()

				.requestMatchers(actuatorEndpoints)
				.permitAll()

				.requestMatchers("/ws/**", "/api/ws")
				.permitAll()

				.requestMatchers(ApiPaths.PROFILE_ENDPOINT)
				.authenticated()

				.requestMatchers(ApiPaths.APPOINTMENT_ENDPOINT, ApiPaths.APPOINTMENT_ENDPOINT + "/**")
				.authenticated()

				.requestMatchers(ApiPaths.NOTIFICATION_ENDPOINT, ApiPaths.NOTIFICATION_ENDPOINT + "/**")
				.authenticated()

				.requestMatchers("/account/password-reset/request", "/account/password-reset/verify",
						"/account/password-reset/confirm", "/account/set-password")
				.permitAll()

				.requestMatchers("/account/change-password")
				.authenticated()

				.requestMatchers("/payment-success.html", "/payment-cancel.html")
				.permitAll()

				.requestMatchers(ApiPaths.WEBHOOK_ENDPOINT)
				.permitAll()

				.requestMatchers(ApiPaths.ONLINE_CONSULTATION_ENDPOINT, ApiPaths.ONLINE_CONSULTATION_ENDPOINT + "/**")
				.authenticated()

				.anyRequest()
				.authenticated())
			.authenticationProvider(authenticationProvider)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

}
