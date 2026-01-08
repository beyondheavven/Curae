package com.vention.medfast.service.service;

import com.vention.medfast.service.dto.ProvidedServiceCreateDto;
import com.vention.medfast.service.dto.ProvidedServiceResponseDto;
import com.vention.medfast.service.mapper.ProvidedServiceMapper;
import com.vention.medfast.service.repository.ProvidedServiceRepository;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.service.repository.specification.ProvidedServiceSpecification;
import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.specialization.repository.SpecializationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProvidedServiceServiceImpl implements ProvidedServiceService {

	private final SpecializationRepository specializationRepository;

	private final ProvidedServiceRepository serviceRepository;

	private final ProvidedServiceMapper serviceMapper;

	@Override
	public List<ProvidedServiceResponseDto> getAll() {
		return serviceRepository.findAll().stream().map(serviceMapper::toDto).toList();
	}

	@Override
	public ProvidedServiceResponseDto getById(Long id) {
		return serviceMapper.toDto(findById(id));
	}

	@Override
	@Transactional
	public long create(ProvidedServiceCreateDto dto) {
		List<Specialization> specializations = processSpecializations(dto.specializationsIds());

		ProvidedService entity = serviceMapper.toEntity(dto);
		entity.setSpecializations(specializations);

		return serviceRepository.save(entity).getId();
	}

	@Override
	public void deleteById(Long id) {
		ProvidedService service = findById(id);
		serviceRepository.delete(service);
	}

	@Override
	@Transactional
	public long update(Long id, ProvidedServiceCreateDto dto) {
		ProvidedService service = findById(id);
		serviceMapper.updateFromDto(dto, service);

		List<Specialization> newSpecializations = processSpecializations(dto.specializationsIds());

		Set<Long> oldIds = service.getSpecializations().stream().map(Specialization::getId).collect(Collectors.toSet());
		Set<Long> newIds = newSpecializations.stream().map(Specialization::getId).collect(Collectors.toSet());

		service.getSpecializations()
			.stream()
			.filter(oldSpec -> !newIds.contains(oldSpec.getId()))
			.forEach(oldSpec -> oldSpec.getServices().remove(service));

		service.getSpecializations().removeIf(oldSpec -> !newIds.contains(oldSpec.getId()));

		newSpecializations.stream().filter(newSpec -> !oldIds.contains(newSpec.getId())).forEach(newSpec -> {
			service.getSpecializations().add(newSpec);
			newSpec.getServices().add(service);
		});

		return service.getId();
	}

	@Override
	public ProvidedService getProvidedServiceById(Long id) {
		return serviceRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Service not found"));
	}

	@Override
	public Page<ProvidedServiceResponseDto> search(String query, Pageable pageable) {
		return serviceRepository.findAll(ProvidedServiceSpecification.searchService(query), pageable)
			.map(serviceMapper::toDto);
	}

	private ProvidedService findById(Long id) {
		return serviceRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Service not found"));
	}

	private List<Specialization> processSpecializations(List<Integer> specializationsIds) {
		List<Long> longIds = specializationsIds.stream().map(Integer::longValue).toList();

		List<Specialization> specializations = specializationRepository.findAllById(longIds);

		Set<Long> foundIds = specializations.stream().map(Specialization::getId).collect(Collectors.toSet());

		List<Long> missingIds = longIds.stream().filter(id -> !foundIds.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new EntityNotFoundException("Specializations not found with ids: " + missingIds);
		}
		return specializations;
	}

}
