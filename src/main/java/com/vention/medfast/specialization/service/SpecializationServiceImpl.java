package com.vention.medfast.specialization.service;

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

@Service
@RequiredArgsConstructor
public class SpecializationServiceImpl implements SpecializationService {

	private final SpecializationRepository specializationRepository;

	private final SpecializationMapper specializationMapper;

	public List<SpecializationResponseDto> getAll() {
		return specializationRepository.findAll().stream().map(specializationMapper::toDto).toList();
	}

	public SpecializationResponseDto getById(Long id) {
		return specializationMapper.toDto(findById(id));
	}

	@Transactional
	public SpecializationResponseDto create(SpecializationCreateDto dto) {
		Specialization saved = specializationRepository.save(specializationMapper.toEntity(dto));
		return specializationMapper.toDto(saved);
	}

	@Transactional
	public void deleteById(Long id) {
		Specialization specialization = findById(id);
		specializationRepository.delete(specialization);
	}

	@Transactional
	public SpecializationResponseDto update(Long id, SpecializationCreateDto dto) {
		Specialization spec = findById(id);
		specializationMapper.updateFromDto(dto, spec);
		return specializationMapper.toDto(spec);
	}

	private Specialization findById(Long id) {
		return specializationRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Specialization not found"));
	}

}
