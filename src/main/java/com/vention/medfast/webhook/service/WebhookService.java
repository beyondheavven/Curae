package com.vention.medfast.webhook.service;

import com.vention.medfast.webhook.dto.WebhookProcessingDto;

import com.stripe.model.Event;

public interface WebhookService {

	WebhookProcessingDto processEvent(Event event);

}
