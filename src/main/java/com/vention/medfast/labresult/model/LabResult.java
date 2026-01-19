package com.vention.medfast.labresult.model;

import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.labtest.model.LabTest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "lab_results")
public class LabResult {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "appointment_id", nullable = false)
	private Appointment appointment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lab_test_id", nullable = false)
	private LabTest labTest;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private LabResultStatus status = LabResultStatus.PROCESSING;

	@Column(name = "result_url")
	private String resultUrl;

	@Column(name = "uploaded_at")
	private LocalDateTime uploadedAt;

}
