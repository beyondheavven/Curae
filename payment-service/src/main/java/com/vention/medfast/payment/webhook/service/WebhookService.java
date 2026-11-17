package com.vention.medfast.payment.webhook.service;

import com.stripe.model.Event;
import com.vention.medfast.payment.webhook.dto.WebhookProcessingDto;

public interface WebhookService {

	WebhookProcessingDto processEvent(Event event);

}
