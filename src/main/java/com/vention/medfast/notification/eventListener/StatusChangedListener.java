package com.vention.medfast.notification.eventListener;

import com.vention.medfast.notification.eventListener.eventModel.StatusChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class StatusChangedListener {

	private final NotificationCreator notificationCreator;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onStatusChanged(StatusChangedEvent e) {
		notificationCreator.handle(e);
	}

}
