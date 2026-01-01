package com.vention.medfast.specialization.model;

import com.vention.medfast.doctor.model.Doctor;
import jakarta.persistence.*;
import com.vention.medfast.service.model.ProvidedService;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "specializations")
public class Specialization {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name", length = 50, nullable = false)
	private String name;

	@Column(name = "description", nullable = false)
	private String description;

	@ManyToMany
	@JoinTable(name = "service_specializations", joinColumns = @JoinColumn(name = "specialization_id"),
			inverseJoinColumns = @JoinColumn(name = "service_id"))
	private List<ProvidedService> services;

	@ManyToMany(mappedBy = "specializations")
	private Set<Doctor> doctors = new HashSet<>();

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Specialization))
			return false;
		return id != null && id.equals(((Specialization) o).getId());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

}
