package com.vention.medfast.visit.model;

import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.service.model.ProvidedService;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "visits")
@PrimaryKeyJoinColumn(name = "appointment_id")
@NoArgsConstructor
public class Visit extends Appointment {

	@Column(name = "diagnosis")
	private String diagnosis;

	@Column(name = "symptoms")
	private String symptoms;

	@Column(name = "medical_report")
	private String medicalReport;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "doctor_id", nullable = false)
	private Doctor doctor;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "service_id", nullable = false)
	private ProvidedService providedService;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "clinic_id", nullable = false)
	private Clinic clinic;

}
