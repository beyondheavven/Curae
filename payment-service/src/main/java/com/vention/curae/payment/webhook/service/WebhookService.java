package com.vention.curae.payment.webhook.service;

import com.stripe.model.Event;
import com.vention.curae.payment.webhook.dto.WebhookProcessingDto;

public interface WebhookService {

	WebhookProcessingDto processEvent(Event event);

}
