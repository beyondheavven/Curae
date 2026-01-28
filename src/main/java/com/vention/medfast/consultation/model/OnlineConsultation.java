package com.vention.medfast.consultation.model;

import com.vention.medfast.appointment.model.Appointment;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import com.vention.medfast.doctor.model.Doctor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "online_consultations")
@PrimaryKeyJoinColumn(name = "appointment_id")
@NoArgsConstructor
public class OnlineConsultation extends Appointment {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "doctor_id", nullable = false)
	private Doctor doctor;

	@Column(name = "meeting_link", nullable = false)
	private String meetingLink;

}
