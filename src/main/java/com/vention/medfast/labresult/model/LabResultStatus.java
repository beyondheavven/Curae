package com.vention.medfast.labresult.model;

/**
 * Represents the status lifecycle of a Lab Result.
 * <p>
 * Flow: <br>
 * - PROCESSING -> READY or FAILED.<br>
 * - FAILED -> PROCESSING (Retry).<br>
 * - READY is a terminal states.<br>
 */
public enum LabResultStatus {

	PROCESSING, READY, FAILED

}
