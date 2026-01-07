package com.vention.medfast.specialization.mapper;

import com.vention.medfast.specialization.dto.SpecializationBaseDto;
import com.vention.medfast.specialization.dto.SpecializationCreateDto;
import com.vention.medfast.specialization.dto.SpecializationResponseDto;
import com.vention.medfast.specialization.model.Specialization;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface SpecializationMapper {

	Specialization toEntity(SpecializationCreateDto dto);

	SpecializationResponseDto toDto(Specialization entity);

	SpecializationBaseDto toBaseDto(Specialization entity);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateFromDto(SpecializationCreateDto dto, @MappingTarget Specialization entity);

}
