package com.vention.medfast.notification.service;

import com.vention.medfast.notification.dto.NotificationDetailsDto;
import com.vention.medfast.notification.dto.NotificationDto;
import com.vention.medfast.notification.mapper.NotificationMapper;
import com.vention.medfast.notification.model.Notification;
import com.vention.medfast.notification.model.NotificationStatus;
import com.vention.medfast.notification.model.NotificationType;
import com.vention.medfast.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationsService {

	private final NotificationRepository notificationRepository;

	private final NotificationMapper notificationMapper;

	@Override
	@Transactional(readOnly = true)
	public Page<NotificationDto> getPatientNotifications(Long patientId, Pageable pageable) {
		return notificationRepository.findByPatientId(patientId, pageable).map(notificationMapper::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	public NotificationDetailsDto getNotificationDetails(Long notificationId) {
		return notificationRepository.findById(notificationId)
			.map(notificationMapper::toDetailsDto)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
	}

	@Override
	@Transactional
	public void markAsRead(Long notificationId, Long patientId) {
		Notification notification = notificationRepository.findById(notificationId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
		if (!patientId.equals(notification.getPatientId())) {
			throw new AccessDeniedException("Not allowed to modify this notification");
		}
		notification.setStatus(NotificationStatus.READ);
		notificationRepository.save(notification);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isOwner(Long notificationId, Long patientId) {
		return notificationRepository.findById(notificationId)
			.map(notification -> patientId.equals(notification.getPatientId()))
			.orElse(false);
	}

	@Override
	@Transactional
	public void create(Long patientId, Long appointmentId, NotificationType type) {
		if (patientId == null) {
			return;
		}
		Notification notification = new Notification();
		notification.setPatientId(patientId);
		notification.setAppointmentId(appointmentId);
		notification.setType(type);
		notification.setStatus(NotificationStatus.UNREAD);
		notificationRepository.save(notification);
	}

}
