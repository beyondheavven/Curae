package com.vention.medfast.appointment.event;

import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.event.AppointmentCreatedEvent;
import com.vention.medfast.event.AppointmentStatusChangedEvent;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentEventPublisher {

	private final KafkaTemplate<Object, Object> kafkaTemplate;

	public void appointmentCreated(Appointment appointment, Long doctorId) {
		kafkaTemplate.send("appointment.created",
				new AppointmentCreatedEvent(appointment.getId(), appointment.getPatientId(), null, null, doctorId, null,
						appointment.getScheduledDate().atTime(appointment.getScheduledTime()), null, Instant.now()));
	}

	public void appointmentStatusChanged(Appointment appointment, Long doctorId, String oldStatus, String newStatus) {
		kafkaTemplate.send("appointment.status-changed", new AppointmentStatusChangedEvent(appointment.getId(),
				appointment.getPatientId(), doctorId, oldStatus, newStatus, Instant.now()));
	}

}
