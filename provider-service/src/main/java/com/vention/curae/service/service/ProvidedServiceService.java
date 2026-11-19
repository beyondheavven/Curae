package com.vention.curae.service.service;

import com.vention.curae.service.dto.ProvidedServiceCreateDto;
import com.vention.curae.service.dto.ProvidedServiceResponseDto;
import com.vention.curae.service.model.ProvidedService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProvidedServiceService {

	List<ProvidedServiceResponseDto> getAll();

	ProvidedServiceResponseDto getById(Long id);

	long create(ProvidedServiceCreateDto dto);

	void deleteById(Long id);

	long update(Long id, ProvidedServiceCreateDto dto);

	ProvidedService getProvidedServiceById(Long id);

	Page<ProvidedServiceResponseDto> search(String query, Pageable pageable);

}