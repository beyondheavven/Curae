package com.vention.curae.payment.catalog.listener;

import com.vention.curae.event.ProvidedServiceDeletedEvent;
import com.vention.curae.event.ProvidedServiceUpsertedEvent;
import com.vention.curae.payment.catalog.model.ServiceCatalog;
import com.vention.curae.payment.catalog.repository.ServiceCatalogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceCatalogKafkaListener {

	private final ServiceCatalogRepository serviceCatalogRepository;

	@Transactional
	@KafkaListener(topics = "provider.service.upserted", groupId = "payment-service-group")
	public void handleServiceUpserted(ProvidedServiceUpsertedEvent event) {
		log.info("Received ProvidedServiceUpsertedEvent for serviceId={}", event.serviceId());
		ServiceCatalog catalog = serviceCatalogRepository.findById(event.serviceId()).orElseGet(ServiceCatalog::new);
		catalog.setId(event.serviceId());
		catalog.setName(event.name());
		catalog.setDescription(event.description());
		catalog.setPrice(event.price());
		serviceCatalogRepository.save(catalog);
	}

	@Transactional
	@KafkaListener(topics = "provider.service.deleted", groupId = "payment-service-group")
	public void handleServiceDeleted(ProvidedServiceDeletedEvent event) {
		log.info("Received ProvidedServiceDeletedEvent for serviceId={}", event.serviceId());
		serviceCatalogRepository.deleteById(event.serviceId());
	}

}