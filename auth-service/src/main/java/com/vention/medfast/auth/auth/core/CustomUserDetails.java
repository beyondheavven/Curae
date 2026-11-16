package com.vention.medfast.auth.auth.core;

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

	private final Long patientId;

	private final Long doctorId;

	public CustomUserDetails(Long id, String email, String passwordHash, RoleType role, Long patientId, Long doctorId) {
		this.id = id;
		this.email = email;
		this.passwordHash = passwordHash;
		this.role = role;
		this.patientId = patientId;
		this.doctorId = doctorId;
	}

	public CustomUserDetails(User user, Long patientId, Long doctorId) {
		this.id = user.getId();
		this.email = user.getEmail();
		this.passwordHash = user.getPasswordHash();
		this.role = user.getRole();
		this.patientId = patientId;
		this.doctorId = doctorId;
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

	public Long getPatientId() {
		return patientId;
	}

	public Long getDoctorId() {
		return doctorId;
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
