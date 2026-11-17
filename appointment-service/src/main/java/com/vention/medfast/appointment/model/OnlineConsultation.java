package com.vention.medfast.appointment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "online_consultations")
@Getter
@Setter
public class OnlineConsultation {

	@Id
	@Column(name = "appointment_id")
	private Long appointmentId;

	@Column(name = "doctor_id")
	private Long doctorId;

	@Column(name = "meeting_link")
	private String meetingLink;

}
