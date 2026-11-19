package com.vention.curae.specialization.mapper;

import com.vention.curae.service.dto.ProvidedServiceBaseDto;
import com.vention.curae.service.model.ProvidedService;
import com.vention.curae.specialization.dto.SpecializationBaseDto;
import com.vention.curae.specialization.dto.SpecializationCreateDto;
import com.vention.curae.specialization.dto.SpecializationResponseDto;
import com.vention.curae.specialization.model.Specialization;
import java.math.BigDecimal;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface SpecializationMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "services", ignore = true)
	@Mapping(target = "doctors", ignore = true)
	Specialization toEntity(SpecializationCreateDto dto);

	SpecializationResponseDto toDto(Specialization entity);

	SpecializationBaseDto toBaseDto(Specialization entity);

	ProvidedServiceBaseDto toServiceBaseDto(ProvidedService entity);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "services", ignore = true)
	@Mapping(target = "doctors", ignore = true)
	void updateFromDto(SpecializationCreateDto dto, @MappingTarget Specialization entity);

	default Double map(BigDecimal value) {
		return value == null ? null : value.doubleValue();
	}

}