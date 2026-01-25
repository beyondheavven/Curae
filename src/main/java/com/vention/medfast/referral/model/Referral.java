package com.vention.medfast.referral.model;

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
@NoArgsConstructor
@Entity
@Table(name = "referrals")
public class Referral {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "patient_id", nullable = false)
	private Patient patient;

	@ManyToOne(optional = false)
	@JoinColumn(name = "issued_by", nullable = false)
	private Doctor issuedBy;

	@ManyToOne(optional = false)
	@JoinColumn(name = "service_id", nullable = false)
	private ProvidedService service;

	@Enumerated(EnumType.STRING)
	@Column(name = "appointment_type", nullable = false, length = 20)
	private AppointmentType appointmentType;

	@Column(nullable = false)
	private LocalDate expiresAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReferralStatus status;

	@CreationTimestamp
	private LocalDateTime createdAt;

}
