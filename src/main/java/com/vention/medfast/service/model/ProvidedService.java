package com.vention.medfast.service.model;

import com.vention.medfast.specialization.model.Specialization;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "services")
public class ProvidedService {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "price", nullable = false)
	private BigDecimal price;

	@Column(name = "duration_minutes", nullable = false)
	private Integer duration;

	@ManyToMany
	@JoinTable(name = "service_specializations", joinColumns = @JoinColumn(name = "service_id"),
			inverseJoinColumns = @JoinColumn(name = "specialization_id"))
	private List<Specialization> specializations;

}
