package com.vention.curae.appointment.state;

import com.vention.curae.appointment.model.AppointmentStatus;
import org.springframework.stereotype.Component;

@Component
public class AppointmentStateContext {

	public boolean isTransitionAllowed(AppointmentStatus current, AppointmentStatus target) {
		if (current == null || target == null || current == target) {
			return false;
		}
		return switch (current) {
			case SCHEDULED -> target == AppointmentStatus.SCHEDULED_CONFIRMED || target == AppointmentStatus.CANCELLED;
			case SCHEDULED_CONFIRMED -> target == AppointmentStatus.IN_CONSULTATION
					|| target == AppointmentStatus.CANCELLED || target == AppointmentStatus.MISSED;
			case IN_CONSULTATION -> target == AppointmentStatus.COMPLETED;
			default -> false;
		};
	}

	public boolean isEditable(AppointmentStatus status) {
		return status == AppointmentStatus.SCHEDULED || status == AppointmentStatus.SCHEDULED_CONFIRMED;
	}

	public boolean isConfirmed(AppointmentStatus status) {
		return status == AppointmentStatus.SCHEDULED_CONFIRMED;
	}

	public boolean isTerminal(AppointmentStatus status) {
		return status == AppointmentStatus.CANCELLED || status == AppointmentStatus.COMPLETED
				|| status == AppointmentStatus.MISSED;
	}

}
