package com.vention.medfast.auth.core;

import com.vention.medfast.user.model.User;
import com.vention.medfast.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) {
		User user = userRepository.findByEmail(username)
			.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		return new CustomUserDetails(user.getId(), user.getEmail(), user.getPasswordHash(), user.getRole());
	}

	public UserDetails loadByUserId(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));

		return new CustomUserDetails(user.getId(), user.getEmail(), user.getPasswordHash(), user.getRole());
	}

}
