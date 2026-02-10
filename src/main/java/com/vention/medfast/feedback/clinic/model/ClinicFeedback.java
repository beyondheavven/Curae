package com.vention.medfast.feedback.clinic.model;

import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.patient.model.Patient;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "clinic_feedbacks")
public class ClinicFeedback {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "rating", nullable = false)
	private Integer rating;

	@Column(name = "summary", nullable = false, length = 500)
	private String summary;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "clinic_id", nullable = false)
	private Clinic clinic;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "patient_id", nullable = false)
	private Patient patient;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false, nullable = false)
	private LocalDateTime createdAt;

}
