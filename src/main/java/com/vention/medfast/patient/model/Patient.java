package com.vention.medfast.patient.model;

import com.vention.medfast.address.model.Address;
import com.vention.medfast.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "patients")
public class Patient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "citizenship", nullable = false, length = 255)
	private String citizenship;

	@Column(name = "gender", nullable = false)
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	@Enumerated(EnumType.STRING)
	private GenderType gender;

	@Column(name = "date_of_birth", nullable = false)
	private LocalDate birthDate;

	@OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "user_id")
	private User user;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "address_id")
	private Address address;

}
