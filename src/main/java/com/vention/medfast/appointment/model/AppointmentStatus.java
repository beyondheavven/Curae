package com.vention.medfast.appointment.model;

/**
 * Represents the lifecycle of a medical appointment.
 * <p>
 * Flow: <br>
 * - SCHEDULED (Draft) -> SCHEDULED_CONFIRMED or CANCELLED. <br>
 * - SCHEDULED_CONFIRMED -> IN_CONSULTATION -> COMPLETED. <br>
 * - COMPLETED, CANCELLED and MISSED are terminal states.
 */
public enum AppointmentStatus {

	SCHEDULED, SCHEDULED_CONFIRMED, CANCELLED, IN_CONSULTATION, COMPLETED, MISSED

}
