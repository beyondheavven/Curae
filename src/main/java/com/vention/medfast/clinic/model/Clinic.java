package com.vention.medfast.clinic.model;

import com.vention.medfast.address.model.Address;
import com.vention.medfast.doctor.model.Doctor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "clinics")
public class Clinic {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	private String logoUrl;

	private String description;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "address_id", nullable = false)
	private Address address;

	@ManyToMany(mappedBy = "clinics")
	private Set<Doctor> doctors = new HashSet<>();

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Clinic))
			return false;
		return id != null && id.equals(((Clinic) o).getId());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

}
