package com.vention.medfast.appointment.referral.model;

import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.service.model.ProvidedService;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.EnumType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "referrals")
@NoArgsConstructor
public class Referral {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "patient_id", nullable = false)
	private Patient patient;

	@ManyToOne(optional = false)
	@JoinColumn(name = "issued_by", nullable = false)
	private Doctor doctor;

	@ManyToOne(optional = false)
	@JoinColumn(name = "service_id", nullable = false)
	private ProvidedService service;

	@Enumerated(EnumType.STRING)
	@Column(name = "appointment_type", nullable = false)
	private AppointmentType appointmentType;

	@Column(name = "expires_at", nullable = false)
	private LocalDate expiresAt;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private ReferralStatus status;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false, nullable = false)
	private LocalDateTime createdAt;

}
