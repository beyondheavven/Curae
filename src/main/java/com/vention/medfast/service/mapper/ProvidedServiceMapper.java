package com.vention.medfast.service.mapper;

import com.vention.medfast.service.dto.ProvidedServiceCreateDto;
import com.vention.medfast.service.dto.ProvidedServiceResponseDto;
import com.vention.medfast.service.model.ProvidedService;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProvidedServiceMapper {

	ProvidedService toEntity(ProvidedServiceCreateDto dto);

	ProvidedServiceResponseDto toDto(ProvidedService entity);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateFromDto(ProvidedServiceCreateDto dto, @MappingTarget ProvidedService entity);

}
