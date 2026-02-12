package com.vention.medfast.auth.service;

public interface SecurityService {

	boolean isAppointmentAvailable(Long appointmentId);

	boolean isAppointmentAvailableExtended(Long appointmentId);

	boolean canUseReferral(Long referralId);

	boolean isNotificationAvailable(Long notificationId);

}
