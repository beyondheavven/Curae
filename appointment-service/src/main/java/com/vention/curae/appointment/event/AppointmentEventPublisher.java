package com.vention.curae.appointment.event;

import com.vention.curae.appointment.model.Appointment;
import com.vention.curae.appointment.snapshot.repository.ServiceSnapshotRepository;
import com.vention.curae.appointment.snapshot.repository.UserSnapshotRepository;
import com.vention.curae.event.AppointmentCreatedEvent;
import com.vention.curae.event.AppointmentStatusChangedEvent;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentEventPublisher {

	private final KafkaTemplate<Object, Object> kafkaTemplate;

	private final UserSnapshotRepository userSnapshotRepository;

	private final ServiceSnapshotRepository serviceSnapshotRepository;

	public void appointmentCreated(Appointment appointment, Long doctorId) {
		kafkaTemplate.send("appointment.created",
				new AppointmentCreatedEvent(appointment.getId(), appointment.getPatientId(), patientEmail(appointment.getPatientId()),
						patientName(appointment.getPatientId()), doctorId, doctorName(doctorId),
						appointment.getScheduledDate().atTime(appointment.getScheduledTime()), null, Instant.now()));
	}

	public void appointmentCreated(Appointment appointment, Long doctorId, Long serviceId) {
		kafkaTemplate.send("appointment.created",
				new AppointmentCreatedEvent(appointment.getId(), appointment.getPatientId(), patientEmail(appointment.getPatientId()),
						patientName(appointment.getPatientId()), doctorId, doctorName(doctorId),
						appointment.getScheduledDate().atTime(appointment.getScheduledTime()), servicePrice(serviceId),
						Instant.now()));
	}

	public void appointmentStatusChanged(Appointment appointment, Long doctorId, String oldStatus, String newStatus) {
		kafkaTemplate.send("appointment.status-changed", new AppointmentStatusChangedEvent(appointment.getId(),
				appointment.getPatientId(), doctorId, oldStatus, newStatus, Instant.now()));
	}

	private String patientEmail(Long patientId) {
		if (patientId == null) {
			return null;
		}
		return userSnapshotRepository.findById(patientId).map(s -> s.getEmail()).orElse(null);
	}

	private String patientName(Long patientId) {
		if (patientId == null) {
			return null;
		}
		return userSnapshotRepository.findById(patientId).map(s -> joinName(s.getFirstName(), s.getLastName())).orElse(null);
	}

	private String doctorName(Long doctorId) {
		if (doctorId == null) {
			return null;
		}
		return userSnapshotRepository.findById(doctorId).map(s -> joinName(s.getFirstName(), s.getLastName())).orElse(null);
	}

	private BigDecimal servicePrice(Long serviceId) {
		if (serviceId == null) {
			return null;
		}
		return serviceSnapshotRepository.findById(serviceId).map(s -> s.getPrice()).orElse(null);
	}

	private String joinName(String firstName, String lastName) {
		String first = firstName == null ? "" : firstName.trim();
		String last = lastName == null ? "" : lastName.trim();
		String full = (first + " " + last).trim();
		return full.isEmpty() ? null : full;
	}

}