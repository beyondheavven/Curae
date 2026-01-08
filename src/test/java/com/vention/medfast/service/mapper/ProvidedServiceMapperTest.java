package com.vention.medfast.service.mapper;

import com.vention.medfast.service.dto.ProvidedServiceBaseDto;
import com.vention.medfast.service.dto.ProvidedServiceCreateDto;
import com.vention.medfast.service.dto.ProvidedServiceResponseDto;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.specialization.dto.SpecializationBaseDto;
import com.vention.medfast.specialization.model.Specialization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProvidedServiceMapperTest {

	private ProvidedServiceMapper providedServiceMapper;

	@BeforeEach
	void setUp() {
		providedServiceMapper = Mappers.getMapper(ProvidedServiceMapper.class);
	}

	@Test
	void shouldMapEntityToDtoWhenInputIsValid() {
		ProvidedService providedService = new ProvidedService();
		providedService.setId(1L);
		providedService.setName("Service name");
		providedService.setDescription("Service description");
		providedService.setPrice(BigDecimal.valueOf(10.5));
		providedService.setDuration(10);

		Specialization spec = new Specialization();
		spec.setId(1L);
		spec.setName("Test specialization");
		spec.setDescription("Specialization description");
		spec.setServices(List.of(providedService));
		providedService.setSpecializations(List.of(spec));
		spec.setServices(List.of(providedService));

		ProvidedServiceResponseDto result = providedServiceMapper.toDto(providedService);

		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(1L);
		assertThat(result.name()).isEqualTo("Service name");
		assertThat(result.description()).isEqualTo("Service description");
		assertThat(result.price()).isEqualTo(10.5);
		assertThat(result.duration()).isEqualTo(10);

		SpecializationBaseDto specDto = result.specializations().getFirst();

		assertThat(specDto).isNotNull();
		assertThat(specDto.id()).isEqualTo(1L);
		assertThat(specDto.name()).isEqualTo("Test specialization");
		assertThat(specDto.description()).isEqualTo("Specialization description");
	}

	@Test
	void shouldMapDtoToEntityWhenInputIsValid() {
		ProvidedServiceCreateDto dto = new ProvidedServiceCreateDto("Test specialization", "Specialization description",
				10.5, 10, new ArrayList<>());

		ProvidedService result = providedServiceMapper.toEntity(dto);

		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo("Test specialization");
		assertThat(result.getDescription()).isEqualTo("Specialization description");
		assertThat(result.getPrice().doubleValue()).isEqualTo(10.5);
		assertThat(result.getDuration()).isEqualTo(10);
	}

	@Test
	void shouldReturnNullWhenEntityIsNull() {
		assertThat(providedServiceMapper.toDto(null)).isNull();
	}

	@Test
	void shouldReturnNullWhenDtoIsNull() {
		assertThat(providedServiceMapper.toEntity(null)).isNull();
	}

	@Test
	void shouldUpdateAllFieldsWhenDtoHasNoNullValues() {
		ProvidedService entity = new ProvidedService();
		entity.setId(1L);
		entity.setName("Old Name");
		entity.setDescription("Old Description");
		entity.setPrice(BigDecimal.valueOf(10.5));
		entity.setDuration(10);

		ProvidedServiceCreateDto dto = new ProvidedServiceCreateDto("New Name", "New Description", 10.0, 30,
				new ArrayList<>());

		providedServiceMapper.updateFromDto(dto, entity);

		assertEquals("New Name", entity.getName());
		assertEquals("New Description", entity.getDescription());
		assertEquals(10.0, entity.getPrice().doubleValue());
		assertEquals(30, entity.getDuration());
		assertEquals(Long.valueOf(1L), entity.getId());
	}

	@Test
	void shouldUpdateOnlyNotNullFieldsWhenNullFieldsPresentInDto() {
		ProvidedService entity = new ProvidedService();
		entity.setId(1L);
		entity.setName("Old Name");
		entity.setDescription("Old Description");
		entity.setPrice(BigDecimal.valueOf(10.5));
		entity.setDuration(10);

		ProvidedServiceCreateDto dto = new ProvidedServiceCreateDto(null, "Updated Description", 10.5, null,
				new ArrayList<>());
		providedServiceMapper.updateFromDto(dto, entity);

		assertEquals("Old Name", entity.getName());
		assertEquals("Updated Description", entity.getDescription());
		assertEquals(10.5, entity.getPrice().doubleValue());
		assertEquals(10, entity.getDuration());
	}

	@Test
	void shouldMapEntityListToDtoList() {
		ProvidedService service1 = new ProvidedService();
		service1.setId(1L);
		service1.setName("Service 1");
		service1.setPrice(BigDecimal.TEN);

		ProvidedService service2 = new ProvidedService();
		service2.setId(2L);
		service2.setName("Service 2");
		service2.setPrice(BigDecimal.ONE);

		List<ProvidedService> entities = List.of(service1, service2);

		List<ProvidedServiceBaseDto> dtos = providedServiceMapper.toDtoList(entities);

		assertThat(dtos.size()).isNotNull().isEqualTo(2);

		assertThat(dtos.get(0).id()).isEqualTo(1L);
		assertThat(dtos.get(0).name()).isEqualTo("Service 1");

		assertThat(dtos.get(1).id()).isEqualTo(2L);
		assertThat(dtos.get(1).name()).isEqualTo("Service 2");
	}

}
