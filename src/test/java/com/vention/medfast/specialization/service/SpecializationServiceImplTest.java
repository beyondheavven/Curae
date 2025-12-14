package com.vention.medfast.specialization.service;

import com.vention.medfast.specialization.dto.SpecializationCreateDto;
import com.vention.medfast.specialization.dto.SpecializationResponseDto;
import com.vention.medfast.specialization.mapper.SpecializationMapper;
import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.specialization.repository.SpecializationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SpecializationServiceImplTest {

	@Mock
	private SpecializationMapper specializationMapper;

	@Mock
	private SpecializationRepository specializationRepository;

	@InjectMocks
	private SpecializationServiceImpl specializationService;

	@Test
	public void shouldCreateSpecializationWhenInputIsValid() {
		SpecializationCreateDto dto = SpecializationCreateDto.builder()
			.name("Cardiologist")
			.description("Heart specialist")
			.build();

		Specialization entity = new Specialization();
		entity.setId(1L);
		entity.setName(dto.name());
		entity.setDescription(dto.description());

		SpecializationResponseDto responseDto = SpecializationResponseDto.builder()
			.id(1L)
			.name(dto.name())
			.description(dto.description())
			.build();

		when(specializationMapper.toEntity(dto)).thenReturn(entity);
		when(specializationMapper.toDto(entity)).thenReturn(responseDto);
		when(specializationRepository.save(entity)).thenReturn(entity);

		SpecializationResponseDto result = specializationService.create(dto);

		assertEquals(Long.valueOf(1L), result.id());
		assertEquals("Cardiologist", result.name());
		assertEquals("Heart specialist", result.description());

		verify(specializationMapper).toEntity(dto);
		verify(specializationMapper).toDto(entity);
		verify(specializationRepository).save(entity);
	}

	@Test
	public void shouldReturnSpecializationWhenInputIdIsValid() {
		Specialization entity = new Specialization();
		entity.setId(1L);
		entity.setName("Cardiologist");
		entity.setDescription("Heart specialist");

		SpecializationResponseDto responseDto = SpecializationResponseDto.builder()
			.id(1L)
			.name(entity.getName())
			.description(entity.getDescription())
			.build();

		when(specializationMapper.toDto(entity)).thenReturn(responseDto);
		when(specializationRepository.findById(1L)).thenReturn(Optional.of(entity));

		SpecializationResponseDto result = specializationService.getById(1L);

		assertEquals(Long.valueOf(1L), result.id());
		assertEquals("Cardiologist", result.name());
		assertEquals("Heart specialist", result.description());

		verify(specializationMapper).toDto(entity);
		verify(specializationRepository).findById(any(Long.class));
	}

	@Test
	public void shouldUpdateSpecializationWhenInputIsValid() {
		SpecializationCreateDto dto = SpecializationCreateDto.builder()
			.name("Cardiologist2")
			.description("Heart specialist2")
			.build();

		Specialization entity = new Specialization();
		entity.setId(1L);
		entity.setName("Cardiologist2");
		entity.setDescription("Heart specialist2");

		SpecializationResponseDto responseDto = SpecializationResponseDto.builder()
			.id(1L)
			.name(entity.getName())
			.description(entity.getDescription())
			.build();

		when(specializationMapper.toDto(entity)).thenReturn(responseDto);
		when(specializationRepository.findById(1L)).thenReturn(Optional.of(entity));

		SpecializationResponseDto result = specializationService.update(1L, dto);

		assertEquals(Long.valueOf(1L), result.id());
		assertEquals("Cardiologist2", result.name());
		assertEquals("Heart specialist2", result.description());

		verify(specializationMapper).toDto(entity);
		verify(specializationRepository).findById(any(Long.class));
	}

	@Test
	public void shouldReturnSpecializationListWhenInputIsValid() {
		Specialization entity = new Specialization();
		entity.setId(1L);
		entity.setName("Cardiologist");
		entity.setDescription("Heart specialist");

		Specialization entity2 = new Specialization();
		entity.setId(2L);
		entity.setName("Cardiologist2");
		entity.setDescription("Heart specialist2");

		List<Specialization> entityResultList = List.of(entity, entity2);

		SpecializationResponseDto responseDto = SpecializationResponseDto.builder()
			.id(1L)
			.name(entity.getName())
			.description(entity.getDescription())
			.build();

		SpecializationResponseDto responseDto2 = SpecializationResponseDto.builder()
			.id(2L)
			.name(entity2.getName())
			.description(entity2.getDescription())
			.build();

		when(specializationMapper.toDto(entity)).thenReturn(responseDto);
		when(specializationMapper.toDto(entity2)).thenReturn(responseDto2);
		when(specializationRepository.findAll()).thenReturn(entityResultList);

		List<SpecializationResponseDto> result = specializationService.getAll();

		assertEquals(2, result.size());

		assertEquals(Long.valueOf(1L), result.getFirst().id());
		assertEquals(Long.valueOf(2L), result.get(1).id());

		verify(specializationRepository).findAll();
		verify(specializationMapper).toDto(entity);
		verify(specializationMapper).toDto(entity2);
	}

	@Test
	public void shouldThrowExceptionWhenGetSpecializationNotFoundById() {
		Long id = 10L;
		when(specializationRepository.findById(id)).thenReturn(Optional.empty());
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
				() -> specializationService.getById(id));

		assert (exception.getMessage().contains("Specialization not found"));
		verify(specializationRepository).findById(id);
		verifyNoMoreInteractions(specializationRepository);
		verifyNoInteractions(specializationMapper);
	}

	@Test
	public void shouldThrowExceptionWhenDeleteSpecializationNotFoundById() {
		Long id = 10L;
		when(specializationRepository.findById(id)).thenReturn(Optional.empty());
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
				() -> specializationService.deleteById(id));

		assert (exception.getMessage().contains("Specialization not found"));
		verify(specializationRepository).findById(id);
		verifyNoMoreInteractions(specializationRepository);
		verifyNoInteractions(specializationMapper);
	}

	@Test
	public void shouldThrowExceptionWhenUpdateSpecializationNotFound() {
		Long id = 10L;
		SpecializationCreateDto dto = SpecializationCreateDto.builder()
			.name("Updated Name")
			.description("Updated Description")
			.build();

		when(specializationRepository.findById(id)).thenReturn(Optional.empty());

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
				() -> specializationService.update(id, dto));

		assert (exception.getMessage().contains("Specialization not found"));

		verify(specializationRepository).findById(id);
		verifyNoMoreInteractions(specializationRepository);
		verifyNoInteractions(specializationMapper);
	}

	@Test
	public void shouldDeleteSpecializationWhenInputIsValid() {
		Long id = 1L;

		Specialization entity = new Specialization();
		entity.setId(id);
		entity.setName("Cardiologist");
		entity.setDescription("Heart specialist");

		when(specializationRepository.findById(id)).thenReturn(Optional.of(entity));

		specializationService.deleteById(id);

		verify(specializationRepository).findById(id);
		verify(specializationRepository).delete(entity);
		verifyNoMoreInteractions(specializationRepository);
	}

}
