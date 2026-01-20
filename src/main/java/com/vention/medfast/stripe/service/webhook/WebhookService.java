package com.vention.medfast.stripe.service.webhook;

import com.vention.medfast.stripe.dto.WebhookProcessingDto;

import com.stripe.model.Event;

public interface WebhookService {

	WebhookProcessingDto processEvent(Event event);

}
