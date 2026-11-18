package com.vention.medfast.specialization.service;

import com.vention.medfast.specialization.dto.SpecializationCreateDto;
import com.vention.medfast.specialization.dto.SpecializationResponseDto;
import com.vention.medfast.specialization.model.Specialization;
import java.util.List;
import java.util.Set;

public interface SpecializationService {

	List<SpecializationResponseDto> getAll();

	SpecializationResponseDto getById(Long id);

	long create(SpecializationCreateDto dto);

	void deleteById(Long id);

	long update(Long id, SpecializationCreateDto dto);

	List<Specialization> findAllByIds(Set<Long> ids);

}