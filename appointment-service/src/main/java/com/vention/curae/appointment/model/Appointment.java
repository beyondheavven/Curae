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
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "appointments")
@Getter
@Setter
public class Appointment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "scheduled_date", nullable = false)
	private LocalDate scheduledDate;

	@Column(name = "scheduled_time", nullable = false)
	private LocalTime scheduledTime;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private AppointmentStatus status;

	@Column(name = "patient_id", nullable = false)
	private Long patientId;

	@Column(name = "created_at", nullable = false, updatable = false, insertable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false, updatable = false, insertable = false)
	private Instant updatedAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private AppointmentType type;

	@Column(name = "referral_id")
	private Long referralId;

}
