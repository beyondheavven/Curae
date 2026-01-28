package com.vention.medfast.appointment.model;

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
@Table(name = "online_consultation")
public class OnlineConsultation {

	@Id
	@Column(name = "appointment_id")
	private Long appointmentId;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "appointment_id")
	private Appointment appointment;

	@Column(name = "meeting_link")
	private String meetingLink;

}
