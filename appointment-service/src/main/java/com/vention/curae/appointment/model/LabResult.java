package com.vention.curae.appointment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "lab_results")
@Getter
@Setter
public class LabResult {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "test_id", nullable = false)
	private Long testId;

	@Column(name = "lab_test_id", nullable = false)
	private Long labTestId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private LabResultStatus status;

	@Column(name = "result_url")
	private String resultUrl;

	@Column(name = "uploaded_at")
	private LocalDateTime uploadedAt;

}
