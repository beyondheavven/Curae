package com.vention.medfast.chat.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "chats", uniqueConstraints = @UniqueConstraint(name = "uk_chat_participants",
		columnNames = { "patient_id", "doctor_id" }))
public class Chat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "patient_id", nullable = false)
	private Long patientId;

	@Column(name = "doctor_id", nullable = false)
	private Long doctorId;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	public static Chat create(Long patientId, Long doctorId) {
		Chat chat = new Chat();
		chat.patientId = patientId;
		chat.doctorId = doctorId;
		return chat;
	}

}
