package com.vention.medfast.specialization.mapper;

import com.vention.medfast.specialization.dto.SpecializationCreateDto;
import com.vention.medfast.specialization.dto.SpecializationResponseDto;
import com.vention.medfast.specialization.model.Specialization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpecializationMapperTest {

	private SpecializationMapper specializationMapper;

	@BeforeEach
	void setUp() {
		specializationMapper = Mappers.getMapper(SpecializationMapper.class);
	}

	@Test
	void shouldMapEntityToDtoWhenInputIsValid() {
		Specialization spec = new Specialization();
		spec.setId(1L);
		spec.setName("Test specialization");
		spec.setDescription("Specialization description");

		SpecializationResponseDto result = specializationMapper.toDto(spec);

		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(1L);
		assertThat(result.name()).isEqualTo("Test specialization");
		assertThat(result.description()).isEqualTo("Specialization description");
	}

	@Test
	void shouldMapDtoToEntityWhenInputIsValid() {
		SpecializationCreateDto dto = SpecializationCreateDto.builder()
			.name("Test specialization")
			.description("Specialization description")
			.build();

		Specialization result = specializationMapper.toEntity(dto);

		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo("Test specialization");
		assertThat(result.getDescription()).isEqualTo("Specialization description");
	}

	@Test
	void shouldReturnNullWhenEntityIsNull() {
		assertThat(specializationMapper.toDto(null)).isNull();
	}

	@Test
	void shouldReturnNullWhenDtoIsNull() {
		assertThat(specializationMapper.toEntity(null)).isNull();
	}

	@Test
	void shouldUpdateAllFieldsWhenDtoHasNoNullValues() {
		Specialization entity = new Specialization();
		entity.setId(1L);
		entity.setName("Old Name");
		entity.setDescription("Old Description");

		SpecializationCreateDto dto = SpecializationCreateDto.builder()
			.name("New Name")
			.description("New Description")
			.build();

		specializationMapper.updateFromDto(dto, entity);

		assertEquals("New Name", entity.getName());
		assertEquals("New Description", entity.getDescription());
		assertEquals(Long.valueOf(1L), entity.getId()); // id should remain unchanged
	}

	@Test
	void shouldUpdateOnlyNotNullFieldsWhenNullFieldsPresentInDto() {
		Specialization entity = new Specialization();
		entity.setId(1L);
		entity.setName("Old Name");
		entity.setDescription("Old Description");

		SpecializationCreateDto dto = SpecializationCreateDto.builder()
			.name(null)
			.description("Updated Description")
			.build();

		specializationMapper.updateFromDto(dto, entity);

		assertEquals("Old Name", entity.getName());
		assertEquals("Updated Description", entity.getDescription());
	}

}
