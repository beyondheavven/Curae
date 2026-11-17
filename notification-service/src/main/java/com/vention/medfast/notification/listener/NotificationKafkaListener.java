package com.vention.medfast.notification.listener;

import com.vention.medfast.event.AppointmentCreatedEvent;
import com.vention.medfast.event.AppointmentStatusChangedEvent;
import com.vention.medfast.event.DoctorWelcomeRequestedEvent;
import com.vention.medfast.event.PaymentCompletedEvent;
import com.vention.medfast.event.PaymentFailedEvent;
import com.vention.medfast.event.PasswordResetCodeRequestedEvent;
import com.vention.medfast.event.UserRegisteredEvent;
import com.vention.medfast.notification.mail.MailSenderService;
import com.vention.medfast.notification.model.NotificationType;
import com.vention.medfast.notification.service.NotificationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationKafkaListener {

	private final MailSenderService mailSenderService;

	private final NotificationsService notificationsService;

	@KafkaListener(topics = "user.registered", groupId = "notification-service-group")
	public void handleUserRegistered(UserRegisteredEvent event) {
		log.info("Received UserRegisteredEvent for email={}", event.email());
		mailSenderService.sendEmailVerification(event.email(), event.verificationToken());
	}

	@KafkaListener(topics = "email.password-reset-requested", groupId = "notification-service-group")
	public void handlePasswordResetRequested(PasswordResetCodeRequestedEvent event) {
		log.info("Received PasswordResetCodeRequestedEvent for email={}", event.email());
		mailSenderService.sendPasswordResetCode(event.email(), event.code());
	}

	@KafkaListener(topics = "email.doctor-welcome-requested", groupId = "notification-service-group")
	public void handleDoctorWelcomeRequested(DoctorWelcomeRequestedEvent event) {
		log.info("Received DoctorWelcomeRequestedEvent for email={}", event.email());
		mailSenderService.sendDoctorWelcome(event.email(), event.temporaryPassword(), event.token());
	}

	@KafkaListener(topics = "appointment.created", groupId = "notification-service-group")
	public void handleAppointmentCreated(AppointmentCreatedEvent event) {
		log.info("Received AppointmentCreatedEvent for appointmentId={}, patient={}", event.appointmentId(),
				event.patientName());
		notificationsService.create(event.patientId(), event.appointmentId(), NotificationType.APPOINTMENT_CREATED);
	}

	@KafkaListener(topics = "appointment.status-changed", groupId = "notification-service-group")
	public void handleAppointmentStatusChanged(AppointmentStatusChangedEvent event) {
		log.info("Received AppointmentStatusChangedEvent for appointmentId={}, newStatus={}", event.appointmentId(),
				event.newStatus());
		NotificationType type = "CANCELLED".equalsIgnoreCase(event.newStatus()) ? NotificationType.APPOINTMENT_CANCELLED
				: NotificationType.APPOINTMENT_DETAILS_CHANGED;
		notificationsService.create(event.patientId(), event.appointmentId(), type);
	}

	@KafkaListener(topics = "payment.completed", groupId = "notification-service-group")
	public void handlePaymentCompleted(PaymentCompletedEvent event) {
		log.info("Received PaymentCompletedEvent for paymentId={}, amount={}{}", event.paymentId(), event.amount(),
				event.currency());
		notificationsService.create(event.patientId(), event.appointmentId(), NotificationType.PAYMENT_COMPLETED);
	}

	@KafkaListener(topics = "payment.failed", groupId = "notification-service-group")
	public void handlePaymentFailed(PaymentFailedEvent event) {
		log.warn("Received PaymentFailedEvent for appointmentId={}, reason={}", event.appointmentId(),
				event.failureReason());
		notificationsService.create(event.patientId(), event.appointmentId(), NotificationType.PAYMENT_FAILED);
	}

}
