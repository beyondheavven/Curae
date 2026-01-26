package com.vention.medfast.notification.service;

import com.vention.medfast.notification.dto.NotificationDetailsDto;
import com.vention.medfast.notification.dto.NotificationDto;
import com.vention.medfast.notification.mapper.NotificationMapper;
import com.vention.medfast.notification.model.Notification;
import com.vention.medfast.notification.model.NotificationStatus;
import com.vention.medfast.notification.repository.NotificationRepository;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationsService {

	private final NotificationRepository notificationRepository;

	private final PatientService patientService;

	private final NotificationMapper notificationMapper;

	@Override
	@Transactional(readOnly = true)
	public Page<NotificationDto> getPatientNotifications(Long patientId, Pageable pageable) {
		Patient patient = patientService.getPatientByUserId(patientId);

		Page<Notification> page = notificationRepository.findByPatientId(patient.getId(), pageable);

		return page.map(notificationMapper::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	public NotificationDetailsDto getPatientNotificationDetails(Long notificationId) {
		Notification notification = notificationRepository.findById(notificationId)
			.orElseThrow(() -> new IllegalArgumentException("Notification not found"));

		return notificationMapper.toDetailsDto(notification);
	}

	@Override
	@Transactional
	public void markAsRead(Long notificationId, Long userId) {

		Notification notification = notificationRepository.findById(notificationId)
			.orElseThrow(() -> new IllegalArgumentException("Notification not found"));
		if (!notification.getPatient().getUser().getId().equals(userId)) {
			throw new AccessDeniedException("Not allowed to modify this notification");
		}

		notification.setStatus(NotificationStatus.READ);
		notificationRepository.save(notification);
	}

}
