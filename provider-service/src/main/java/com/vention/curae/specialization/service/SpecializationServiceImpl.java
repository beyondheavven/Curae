package com.vention.curae.specialization.service;

import com.vention.curae.service.model.ProvidedService;
import com.vention.curae.service.repository.ProvidedServiceRepository;
import com.vention.curae.specialization.dto.SpecializationCreateDto;
import com.vention.curae.specialization.dto.SpecializationResponseDto;
import com.vention.curae.specialization.mapper.SpecializationMapper;
import com.vention.curae.specialization.model.Specialization;
import com.vention.curae.specialization.repository.SpecializationRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpecializationServiceImpl implements SpecializationService {

	private final ProvidedServiceRepository serviceRepository;

	private final SpecializationRepository specializationRepository;

	private final SpecializationMapper specializationMapper;

	@Override
	@Transactional(readOnly = true)
	public List<SpecializationResponseDto> getAll() {
		return specializationRepository.findAll().stream().map(specializationMapper::toDto).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public SpecializationResponseDto getById(Long id) {
		return specializationMapper.toDto(findById(id));
	}

	@Override
	@Transactional
	public long create(SpecializationCreateDto dto) {
		Specialization entity = specializationMapper.toEntity(dto);
		List<ProvidedService> services = processServices(dto.serviceIds());
		entity.setServices(services);
		services.forEach(s -> {
			if (!s.getSpecializations().contains(entity)) {
				s.getSpecializations().add(entity);
			}
		});
		Specialization saved = specializationRepository.save(entity);
		serviceRepository.saveAll(services);
		return saved.getId();
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		specializationRepository.delete(findById(id));
	}

	@Override
	@Transactional
	public long update(Long id, SpecializationCreateDto dto) {
		Specialization specialization = findById(id);
		specializationMapper.updateFromDto(dto, specialization);

		List<ProvidedService> newServices = processServices(dto.serviceIds());
		Set<Long> newIds = newServices.stream().map(ProvidedService::getId).collect(Collectors.toSet());

		specialization.getServices()
			.stream()
			.filter(oldService -> !newIds.contains(oldService.getId()))
			.forEach(oldService -> oldService.getSpecializations().remove(specialization));
		specialization.getServices().removeIf(oldService -> !newIds.contains(oldService.getId()));

		newServices.stream()
			.filter(newService -> specialization.getServices()
				.stream()
				.noneMatch(existing -> existing.getId().equals(newService.getId())))
			.forEach(newService -> {
				specialization.getServices().add(newService);
				newService.getSpecializations().add(specialization);
			});

		serviceRepository.saveAll(specialization.getServices());
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
		List<Long> missingIds = longIds.stream().filter(i -> !foundIds.contains(i)).toList();
		if (!missingIds.isEmpty()) {
			throw new EntityNotFoundException("Services not found with ids: " + missingIds);
		}
		return services;
	}

}