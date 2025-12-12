package com.vention.medfast.specialization.service;

import com.vention.medfast.specialization.dto.SpecializationCreateDto;
import com.vention.medfast.specialization.dto.SpecializationResponseDto;

import java.util.List;

public interface SpecializationService {

	List<SpecializationResponseDto> getAll();

	SpecializationResponseDto getById(Long id);

	SpecializationResponseDto create(SpecializationCreateDto dto);

	void deleteById(Long id);

	SpecializationResponseDto update(Long id, SpecializationCreateDto dto);

}
