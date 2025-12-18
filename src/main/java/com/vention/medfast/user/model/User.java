package com.vention.medfast.user.model;

import com.vention.medfast.refreshtoken.model.RefreshToken;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "email", nullable = false, length = 255)
	private String email;

	@Column(name = "password_hash", nullable = false, length = 255)
	private String passwordHash;

	@Column(name = "first_name", nullable = false, length = 255)
	private String firstName;

	@Column(name = "last_name", nullable = false, length = 255)
	private String lastName;

	@Column(name = "profile_url", length = 255)
	private String profileUrl;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private RoleType role;

	@OneToMany(mappedBy = "users")
	private Set<RefreshToken> refreshTokens = new LinkedHashSet<>();

}
