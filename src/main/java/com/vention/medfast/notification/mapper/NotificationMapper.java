package com.vention.medfast.notification.mapper;

import com.vention.medfast.notification.dto.NotificationDetailsDto;
import com.vention.medfast.notification.dto.NotificationDto;
import com.vention.medfast.notification.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

	@Mapping(target = "appointmentId", source = "appointment.id")
	NotificationDto toDto(Notification notification);

	@Mapping(target = "appointmentId", source = "appointment.id")
	NotificationDetailsDto toDetailsDto(Notification notification);

}
