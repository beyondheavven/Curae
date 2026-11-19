package com.vention.curae.provider.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_snapshots")
public class UserSnapshot {

	@Id
	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Column(name = "last_name", nullable = false)
	private String lastName;

	@Column(name = "birth_date")
	private LocalDate birthDate;

	@Column(name = "profile_url")
	private String profileUrl;

	@Column(name = "status", nullable = false)
	private String status;

}
