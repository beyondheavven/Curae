package com.vention.curae.appointment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "referrals")
@Getter
@Setter
public class Referral {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "patient_id", nullable = false)
	private Long patientId;

	@Column(name = "issued_by", nullable = false)
	private Long issuedBy;

	@Column(name = "service_id", nullable = false)
	private Long serviceId;

	@Enumerated(EnumType.STRING)
	@Column(name = "appointment_type", nullable = false, length = 20)
	private AppointmentType appointmentType;

	@Column(name = "expires_at", nullable = false)
	private LocalDate expiresAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ReferralStatus status;

	@Column(name = "created_at", nullable = false, updatable = false, insertable = false)
	private Instant createdAt;

}
