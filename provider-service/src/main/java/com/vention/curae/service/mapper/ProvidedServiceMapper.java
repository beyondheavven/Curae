package com.vention.curae.service.mapper;

import com.vention.curae.service.dto.ProvidedServiceBaseDto;
import com.vention.curae.service.dto.ProvidedServiceCreateDto;
import com.vention.curae.service.dto.ProvidedServiceResponseDto;
import com.vention.curae.service.model.ProvidedService;
import com.vention.curae.specialization.dto.SpecializationBaseDto;
import com.vention.curae.specialization.model.Specialization;
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