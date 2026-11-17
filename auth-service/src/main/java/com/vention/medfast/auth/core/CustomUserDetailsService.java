package com.vention.medfast.auth.core;

import com.vention.medfast.user.model.RoleType;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserService userService;

	@Override
	public UserDetails loadUserByUsername(String username) {
		String normalizedUsername = username.toLowerCase();
		User user = userService.findByUsername(normalizedUsername);
		return loadUserEntity(user);
	}

	public UserDetails loadUserEntity(User user) {
		return new CustomUserDetails(user);
	}

}
