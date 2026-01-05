package com.vention.medfast.visit.model;

import com.vention.medfast.appointment.model.Appointment;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "visits")
public class Visit {

	@Id
	@Column(name = "appointment_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
	@MapsId
	@JoinColumn(name = "appointment_id")
	private Appointment appointment;

	@Column(name = "diagnosis", nullable = false)
	private String diagnosis;

	@Column(name = "symptoms", nullable = false)
	private String symptoms;

	@Column(name = "medical_report", nullable = false)
	private String medicalReport;

}
