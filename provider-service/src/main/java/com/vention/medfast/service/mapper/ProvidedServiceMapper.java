package com.vention.medfast.service.mapper;

import com.vention.medfast.service.dto.ProvidedServiceBaseDto;
import com.vention.medfast.service.dto.ProvidedServiceCreateDto;
import com.vention.medfast.service.dto.ProvidedServiceResponseDto;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.specialization.dto.SpecializationBaseDto;
import com.vention.medfast.specialization.model.Specialization;
import java.math.BigDecimal;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProvidedServiceMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "specializations", ignore = true)
	@Mapping(target = "price", expression = "java(dto.price() == null ? null : java.math.BigDecimal.valueOf(dto.price()))")
	ProvidedService toEntity(ProvidedServiceCreateDto dto);

	@Mapping(target = "price", expression = "java(entity.getPrice() == null ? null : entity.getPrice().doubleValue())")
	ProvidedServiceResponseDto toDto(ProvidedService entity);

	SpecializationBaseDto toSpecializationBaseDto(Specialization entity);

	ProvidedServiceBaseDto toBaseDto(ProvidedService entity);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "specializations", ignore = true)
	@Mapping(target = "price", expression = "java(dto.price() == null ? null : java.math.BigDecimal.valueOf(dto.price()))")
	void updateFromDto(ProvidedServiceCreateDto dto, @MappingTarget ProvidedService entity);

	List<ProvidedServiceBaseDto> toDtoList(List<ProvidedService> serviceEntities);

}