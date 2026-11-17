package com.vention.medfast.auth.core;

import com.vention.medfast.user.model.RoleType;
import com.vention.medfast.user.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

	private final Long id;

	private final String email;

	private final String passwordHash;

	private final RoleType role;

	public CustomUserDetails(Long id, String email, String passwordHash, RoleType role) {
		this.id = id;
		this.email = email;
		this.passwordHash = passwordHash;
		this.role = role;
	}

	public CustomUserDetails(User user) {
		this.id = user.getId();
		this.email = user.getEmail();
		this.passwordHash = user.getPasswordHash();
		this.role = user.getRole();
	}

	public Long getId() {
		return id;
	}

	public RoleType getRoleEnum() {
		return role;
	}

	public String getRoleName() {
		return "ROLE_" + role.name();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(getRoleName()));
	}

	@Override
	public String getPassword() {
		return passwordHash;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
