package com.vention.medfast.notification.eventListener;

import com.vention.medfast.appointment.service.AppointmentService;
import com.vention.medfast.notification.eventListener.eventModel.StatusChangedEvent;
import com.vention.medfast.notification.model.Notification;
import com.vention.medfast.notification.model.NotificationStatus;
import com.vention.medfast.notification.repository.NotificationRepository;
import com.vention.medfast.patient.service.PatientService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class NotificationCreator {

	private final NotificationRepository notificationRepository;

	private final PatientService patientService;

	private final AppointmentService appointmentService;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handle(StatusChangedEvent e) {
		Notification notification = new Notification();
		notification.setPatient(patientService.getById(e.patientId()));
		notification.setAppointment(appointmentService.findAppointment(e.appointmentId()));
		notification.setType(e.type());
		notification.setStatus(NotificationStatus.UNREAD);

		notificationRepository.save(notification);
	}

}
