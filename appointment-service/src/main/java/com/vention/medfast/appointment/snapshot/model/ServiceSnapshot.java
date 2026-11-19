package com.vention.medfast.appointment.snapshot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "service_snapshots")
public class ServiceSnapshot {

	@Id
	@Column(name = "service_id")
	private Long serviceId;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "price")
	private BigDecimal price;

	@Column(name = "duration_minutes")
	private Integer durationMinutes;

}