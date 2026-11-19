package com.vention.curae.notification.repository;

import com.vention.curae.notification.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	Page<Notification> findByPatientId(Long patientId, Pageable pageable);

}
