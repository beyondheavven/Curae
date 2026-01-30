package com.vention.medfast.appointment.state;

import com.vention.medfast.appointment.model.AppointmentStatus;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static com.vention.medfast.appointment.model.AppointmentStatus.CANCELLED;
import static com.vention.medfast.appointment.model.AppointmentStatus.COMPLETED;
import static com.vention.medfast.appointment.model.AppointmentStatus.IN_CONSULTATION;
import static com.vention.medfast.appointment.model.AppointmentStatus.MISSED;
import static com.vention.medfast.appointment.model.AppointmentStatus.SCHEDULED;
import static com.vention.medfast.appointment.model.AppointmentStatus.SCHEDULED_CONFIRMED;

/**
 * Centralizes validation logic for {@link AppointmentStatus} transitions and
 * capabilities.
 */
@Component
public class AppointmentStateContext {

	private static final Map<AppointmentStatus, Set<AppointmentStatus>> TRANSITIONS = new EnumMap<>(
			AppointmentStatus.class);

	static {
		TRANSITIONS.put(SCHEDULED, Set.of(SCHEDULED_CONFIRMED, CANCELLED));

		TRANSITIONS.put(SCHEDULED_CONFIRMED, Set.of(IN_CONSULTATION, CANCELLED, MISSED));

		TRANSITIONS.put(IN_CONSULTATION, Set.of(COMPLETED));

	}

	public boolean isTransitionAllowed(AppointmentStatus current, AppointmentStatus target) {
		if (current == target)
			return true;
		return TRANSITIONS.getOrDefault(current, Collections.emptySet()).contains(target);
	}

	public boolean isEditable(AppointmentStatus status) {
		return status == SCHEDULED || status == SCHEDULED_CONFIRMED;
	}

	public boolean isConfirmed(AppointmentStatus status) {
		return status == SCHEDULED_CONFIRMED;
	}

}
