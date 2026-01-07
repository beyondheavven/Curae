package com.vention.medfast.service.service;

import com.vention.medfast.service.dto.ProvidedServiceCreateDto;
import com.vention.medfast.service.dto.ProvidedServiceResponseDto;
import com.vention.medfast.service.model.ProvidedService;

import java.util.List;

public interface ProvidedServiceService {

	List<ProvidedServiceResponseDto> getAll();

	ProvidedServiceResponseDto getById(Long id);

	long create(ProvidedServiceCreateDto dto);

	void deleteById(Long id);

	long update(Long id, ProvidedServiceCreateDto dto);

	ProvidedService getProvidedServiceById(Long id);

}
