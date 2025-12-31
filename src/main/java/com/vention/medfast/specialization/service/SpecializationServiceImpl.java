package com.vention.medfast.specialization.service;

import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.service.repository.ProvidedServiceRepository;
import com.vention.medfast.specialization.dto.SpecializationCreateDto;
import com.vention.medfast.specialization.dto.SpecializationResponseDto;
import com.vention.medfast.specialization.mapper.SpecializationMapper;
import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.specialization.repository.SpecializationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecializationServiceImpl implements SpecializationService {

	private final ProvidedServiceRepository serviceRepository;

	private final SpecializationRepository specializationRepository;

	private final SpecializationMapper specializationMapper;

	public List<SpecializationResponseDto> getAll() {
		return specializationRepository.findAll().stream().map(specializationMapper::toDto).toList();
	}

	public SpecializationResponseDto getById(Long id) {
		return specializationMapper.toDto(findById(id));
	}

	@Override
	@Transactional
	public long create(SpecializationCreateDto dto) {
		Specialization entity = specializationMapper.toEntity(dto);
		entity.setServices(processServices(dto.serviceIds()));
		Specialization saved = specializationRepository.save(entity);
		return saved.getId();
	}

	@Override
	public void deleteById(Long id) {
		Specialization specialization = findById(id);
		specializationRepository.delete(specialization);
	}

	@Override
	@Transactional
	public long update(Long id, SpecializationCreateDto dto) {
		Specialization specialization = findById(id);
		specializationMapper.updateFromDto(dto, specialization);

		List<ProvidedService> newServices = processServices(dto.serviceIds());

		Set<Long> oldIds = specialization.getServices()
			.stream()
			.map(ProvidedService::getId)
			.collect(Collectors.toSet());
		Set<Long> newIds = newServices.stream().map(ProvidedService::getId).collect(Collectors.toSet());

		specialization.getServices()
			.stream()
			.filter(oldService -> !newIds.contains(oldService.getId()))
			.forEach(oldService -> oldService.getSpecializations().remove(specialization));

		specialization.getServices().removeIf(oldService -> !newIds.contains(oldService.getId()));

		newServices.stream().filter(newService -> !oldIds.contains(newService.getId())).forEach(newService -> {
			specialization.getServices().add(newService);
			newService.getSpecializations().add(specialization);
		});

		return specialization.getId();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Specialization> findAllByIds(Set<Long> ids) {
		return specializationRepository.findAllById(ids);
	}

	private Specialization findById(Long id) {
		return specializationRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Specialization not found"));
	}

	private List<ProvidedService> processServices(List<Integer> serviceIds) {
		List<Long> longIds = serviceIds.stream().map(Integer::longValue).toList();

		List<ProvidedService> services = serviceRepository.findAllById(longIds);

		Set<Long> foundIds = services.stream().map(ProvidedService::getId).collect(Collectors.toSet());

		List<Long> missingIds = longIds.stream().filter(id -> !foundIds.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new EntityNotFoundException("Services not found with ids: " + missingIds);
		}
		return services;
	}

}
