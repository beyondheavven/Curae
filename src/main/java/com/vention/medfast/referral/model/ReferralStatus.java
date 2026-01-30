package com.vention.medfast.referral.model;

/**
 * Represents the state lifecycle of a Referral.
 * <p>
 * Flow: <br>
 * - ACTIVE -> USED or EXPIRED.<br>
 * - USED and EXPIRED are terminal states
 */
public enum ReferralStatus {

	ACTIVE, EXPIRED, USED

}
