package com.vention.medfast.notification.listener;

import com.vention.medfast.event.AppointmentCreatedEvent;
import com.vention.medfast.event.AppointmentStatusChangedEvent;
import com.vention.medfast.event.PaymentCompletedEvent;
import com.vention.medfast.event.PaymentFailedEvent;
import com.vention.medfast.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationKafkaListener {

    @KafkaListener(topics = "user.registered", groupId = "notification-service-group")
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("Received UserRegisteredEvent for email={}", event.email());
        // TODO: Send verification email using Thymeleaf template and JavaMailSender
    }

    @KafkaListener(topics = "appointment.created", groupId = "notification-service-group")
    public void handleAppointmentCreated(AppointmentCreatedEvent event) {
        log.info("Received AppointmentCreatedEvent for appointmentId={}, patient={}",
                event.appointmentId(), event.patientName());
        // TODO: Send appointment confirmation notification
    }

    @KafkaListener(topics = "appointment.status-changed", groupId = "notification-service-group")
    public void handleAppointmentStatusChanged(AppointmentStatusChangedEvent event) {
        log.info("Received AppointmentStatusChangedEvent for appointmentId={}, newStatus={}",
                event.appointmentId(), event.newStatus());
    }

    @KafkaListener(topics = "payment.completed", groupId = "notification-service-group")
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("Received PaymentCompletedEvent for paymentId={}, amount={}{}",
                event.paymentId(), event.amount(), event.currency());
    }

    @KafkaListener(topics = "payment.failed", groupId = "notification-service-group")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.warn("Received PaymentFailedEvent for appointmentId={}, reason={}",
                event.appointmentId(), event.failureReason());
    }
}
