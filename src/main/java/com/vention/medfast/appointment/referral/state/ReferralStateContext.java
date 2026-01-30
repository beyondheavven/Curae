package com.vention.medfast.appointment.referral.state;

import com.vention.medfast.appointment.referral.model.Referral;
import com.vention.medfast.appointment.referral.model.ReferralStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static com.vention.medfast.appointment.referral.model.ReferralStatus.ACTIVE;
import static com.vention.medfast.appointment.referral.model.ReferralStatus.EXPIRED;
import static com.vention.medfast.appointment.referral.model.ReferralStatus.USED;

/**
 * Centralizes validation logic for {@link ReferralStatus} transitions.
 */
@Component
public class ReferralStateContext {

	private static final Map<ReferralStatus, Set<ReferralStatus>> TRANSITIONS = new EnumMap<>(ReferralStatus.class);

	static {
		TRANSITIONS.put(ACTIVE, Set.of(USED, EXPIRED));
	}

	public boolean isTransitionAllowed(ReferralStatus current, ReferralStatus target) {
		if (current == target)
			return true;
		return TRANSITIONS.getOrDefault(current, Collections.emptySet()).contains(target);
	}

	public boolean isAvailable(Referral referral) {
		if (referral.getStatus() != ACTIVE) {
			return false;
		}
		return !referral.getExpiresAt().isBefore(LocalDate.now());
	}

}
