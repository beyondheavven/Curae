package com.vention.curae.appointment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tests")
@Getter
@Setter
public class Test {

	@Id
	@Column(name = "appointment_id")
	private Long appointmentId;

	@Column(name = "clinic_id")
	private Long clinicId;

}
