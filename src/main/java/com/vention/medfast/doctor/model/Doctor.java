package com.vention.medfast.doctor.model;

import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.user.model.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "doctors")
public class Doctor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "bio", nullable = false, length = 255)
	private String bio;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToOne(mappedBy = "doctor", cascade = CascadeType.ALL, optional = false)
	private DoctorDetails details;

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "doctor_clinics", joinColumns = @JoinColumn(name = "doctor_id"),
			inverseJoinColumns = @JoinColumn(name = "clinic_id"))
	private Set<Clinic> clinics = new HashSet<>();

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "doctor_specializations", joinColumns = @JoinColumn(name = "doctor_id"),
			inverseJoinColumns = @JoinColumn(name = "specialization_id"))
	private Set<Specialization> specializations = new HashSet<>();

	public Doctor setDetails(DoctorDetails details) {
		if (details == null) {
			if (this.details != null) {
				this.details.setDoctor(null);
			}
		}
		else {
			details.setDoctor(this);
		}
		this.details = details;
		return this;
	}

	public void addClinic(Clinic clinic) {
		this.clinics.add(clinic);
		clinic.getDoctors().add(this);
	}

	public void removeClinic(Clinic clinic) {
		this.clinics.remove(clinic);
		clinic.getDoctors().remove(this);
	}

	public void addSpecialization(Specialization specialization) {
		this.specializations.add(specialization);
		specialization.getDoctors().add(this);
	}

	public void removeSpecialization(Specialization specialization) {
		this.specializations.remove(specialization);
		specialization.getDoctors().remove(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Doctor))
			return false;
		return id != null && id.equals(((Doctor) o).getId());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

}
