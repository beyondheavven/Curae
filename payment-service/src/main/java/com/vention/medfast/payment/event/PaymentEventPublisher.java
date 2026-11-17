package com.vention.medfast.payment.event;

import com.vention.medfast.event.PaymentCompletedEvent;
import com.vention.medfast.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

	private final KafkaTemplate<Object, Object> kafkaTemplate;

	public void paymentCompleted(PaymentCompletedEvent event) {
		kafkaTemplate.send("payment.completed", event);
	}

	public void paymentFailed(PaymentFailedEvent event) {
		kafkaTemplate.send("payment.failed", event);
	}

}
