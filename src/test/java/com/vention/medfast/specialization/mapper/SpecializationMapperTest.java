package com.vention.medfast.specialization.mapper;

import com.vention.medfast.service.dto.ProvidedServiceBaseDto;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.specialization.dto.SpecializationCreateDto;
import com.vention.medfast.specialization.dto.SpecializationResponseDto;
import com.vention.medfast.specialization.model.Specialization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
		ProvidedService service = new ProvidedService();
		service.setId(1L);
		service.setName("Service name");
		service.setDescription("Service description");
		service.setPrice(BigDecimal.valueOf(10.5));
		service.setDuration(10);

		Specialization spec = new Specialization();
		spec.setId(1L);
		spec.setName("Test specialization");
		spec.setDescription("Specialization description");
		spec.setServices(List.of(service));
		service.setSpecializations(List.of(spec));

		SpecializationResponseDto result = specializationMapper.toDto(spec);

		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(1L);
		assertThat(result.name()).isEqualTo("Test specialization");
		assertThat(result.description()).isEqualTo("Specialization description");

		ProvidedServiceBaseDto serviceDto = result.services().getFirst();
		assertThat(serviceDto.id()).isEqualTo(1L);
		assertThat(serviceDto.name()).isEqualTo("Service name");
		assertThat(serviceDto.description()).isEqualTo("Service description");
	}

	@Test
	void shouldMapDtoToEntityWhenInputIsValid() {
		SpecializationCreateDto dto = new SpecializationCreateDto("Test specialization", "Specialization description",
				new ArrayList<>());

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

		SpecializationCreateDto dto = new SpecializationCreateDto("New Name", "New Description", new ArrayList<>());

		specializationMapper.updateFromDto(dto, entity);

		assertEquals("New Name", entity.getName());
		assertEquals("New Description", entity.getDescription());
		assertEquals(Long.valueOf(1L), entity.getId());
	}

	@Test
	void shouldUpdateOnlyNotNullFieldsWhenNullFieldsPresentInDto() {
		Specialization entity = new Specialization();
		entity.setId(1L);
		entity.setName("Old Name");
		entity.setDescription("Old Description");

		SpecializationCreateDto dto = new SpecializationCreateDto(null, "Updated Description", new ArrayList<>());
		specializationMapper.updateFromDto(dto, entity);

		assertEquals("Old Name", entity.getName());
		assertEquals("Updated Description", entity.getDescription());
	}

}
