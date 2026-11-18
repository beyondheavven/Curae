package com.vention.medfast.service.service;

import com.vention.medfast.event.ProvidedServiceDeletedEvent;
import com.vention.medfast.event.ProvidedServiceUpsertedEvent;
import com.vention.medfast.service.dto.ProvidedServiceCreateDto;
import com.vention.medfast.service.dto.ProvidedServiceResponseDto;
import com.vention.medfast.service.mapper.ProvidedServiceMapper;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.service.repository.ProvidedServiceRepository;
import com.vention.medfast.service.repository.specification.ProvidedServiceSpecification;
import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.specialization.repository.SpecializationRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProvidedServiceServiceImpl implements ProvidedServiceService {

	private final SpecializationRepository specializationRepository;

	private final ProvidedServiceRepository serviceRepository;

	private final ProvidedServiceMapper serviceMapper;

	private final KafkaTemplate<Object, Object> kafkaTemplate;

	@Override
	@Transactional(readOnly = true)
	public List<ProvidedServiceResponseDto> getAll() {
		return serviceRepository.findAll().stream().map(serviceMapper::toDto).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public ProvidedServiceResponseDto getById(Long id) {
		return serviceMapper.toDto(findById(id));
	}

	@Override
	@Transactional
	public long create(ProvidedServiceCreateDto dto) {
		ProvidedService entity = serviceMapper.toEntity(dto);
		entity.setSpecializations(processSpecializations(dto.specializationsIds()));
		ProvidedService saved = serviceRepository.save(entity);
		publishUpsert(saved);
		return saved.getId();
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		ProvidedService service = findById(id);
		serviceRepository.delete(service);
		kafkaTemplate.send("provider.service.deleted", new ProvidedServiceDeletedEvent(id, Instant.now()));
	}

	@Override
	@Transactional
	public long update(Long id, ProvidedServiceCreateDto dto) {
		ProvidedService service = findById(id);
		serviceMapper.updateFromDto(dto, service);

		List<Specialization> newSpecializations = processSpecializations(dto.specializationsIds());
		Set<Long> oldIds = service.getSpecializations().stream().map(Specialization::getId).collect(Collectors.toSet());
		Set<Long> newIds = newSpecializations.stream().map(Specialization::getId).collect(Collectors.toSet());
		service.getSpecializations().removeIf(oldSpec -> !newIds.contains(oldSpec.getId()));
		newSpecializations.stream().filter(newSpec -> !oldIds.contains(newSpec.getId()))
				.forEach(service.getSpecializations()::add);

		ProvidedService saved = serviceRepository.save(service);
		publishUpsert(saved);
		return saved.getId();
	}

	@Override
	@Transactional(readOnly = true)
	public ProvidedService getProvidedServiceById(Long id) {
		return findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ProvidedServiceResponseDto> search(String query, Pageable pageable) {
		return serviceRepository.findAll(ProvidedServiceSpecification.searchService(query), pageable)
				.map(serviceMapper::toDto);
	}

	private void publishUpsert(ProvidedService service) {
		kafkaTemplate.send("provider.service.upserted", new ProvidedServiceUpsertedEvent(service.getId(),
				service.getName(), service.getDescription(), service.getPrice(), service.getDuration(), Instant.now()));
	}

	private ProvidedService findById(Long id) {
		return serviceRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Service not found"));
	}

	private List<Specialization> processSpecializations(List<Integer> specializationsIds) {
		List<Long> longIds = specializationsIds.stream().map(Integer::longValue).toList();
		List<Specialization> specializations = specializationRepository.findAllById(longIds);
		Set<Long> foundIds = specializations.stream().map(Specialization::getId).collect(Collectors.toSet());
		List<Long> missingIds = longIds.stream().filter(i -> !foundIds.contains(i)).toList();
		if (!missingIds.isEmpty()) {
			throw new EntityNotFoundException("Specializations not found with ids: " + missingIds);
		}
		return specializations;
	}

}