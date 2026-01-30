package com.vention.medfast.labresult.context;

import com.vention.medfast.labresult.model.LabResultStatus;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static com.vention.medfast.labresult.model.LabResultStatus.FAILED;
import static com.vention.medfast.labresult.model.LabResultStatus.PROCESSING;
import static com.vention.medfast.labresult.model.LabResultStatus.READY;

/**
 * Centralizes validation logic for {@link LabResultStatus} transitions.
 */
@Component
public class LabResultStateContext {

	private static final Map<LabResultStatus, Set<LabResultStatus>> TRANSITIONS = new EnumMap<>(LabResultStatus.class);

	static {
		TRANSITIONS.put(PROCESSING, Set.of(READY, FAILED));
		TRANSITIONS.put(FAILED, Set.of(PROCESSING));
	}

	public boolean isTransitionAllowed(LabResultStatus current, LabResultStatus target) {
		if (current == target)
			return true;
		return TRANSITIONS.getOrDefault(current, Collections.emptySet()).contains(target);
	}

}
