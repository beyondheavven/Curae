package com.vention.curae.appointment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "visits")
@Getter
@Setter
public class Visit {

	@Id
	@Column(name = "appointment_id")
	private Long appointmentId;

	@Column(columnDefinition = "text")
	private String diagnosis;

	@Column(columnDefinition = "text")
	private String symptoms;

	@Column(name = "medical_report", columnDefinition = "text")
	private String medicalReport;

	@Column(name = "clinic_id", nullable = false)
	private Long clinicId;

	@Column(name = "doctor_id")
	private Long doctorId;

	@Column(name = "service_id")
	private Long serviceId;

	@Column(name = "doctor_first_name")
	private String doctorFirstName;

	@Column(name = "doctor_last_name")
	private String doctorLastName;

	@Column(name = "clinic_name")
	private String clinicName;

	@Column
	private String city;

}
