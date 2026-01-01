package com.vention.medfast.specialization.service;

import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.service.repository.ProvidedServiceRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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

	@Mock
	private ProvidedServiceRepository providedServiceRepository;

	@InjectMocks
	private SpecializationServiceImpl specializationService;

	@Test
	public void shouldCreateSpecializationWhenInputIsValid() {
		SpecializationCreateDto dto = new SpecializationCreateDto("Cardiologist", "Heart specialist", List.of(1));

		Specialization entity = new Specialization();
		entity.setId(1L);
		entity.setName(dto.name());
		entity.setDescription(dto.description());

		ProvidedService service = new ProvidedService();
		service.setId(1L);

		when(specializationMapper.toEntity(dto)).thenReturn(entity);
		when(providedServiceRepository.findAllById(anyList())).thenReturn(List.of(service));
		when(specializationRepository.save(entity)).thenReturn(entity);

		long resultId = specializationService.create(dto);

		assertEquals(1L, resultId);

		verify(specializationMapper).toEntity(dto);
		verify(providedServiceRepository).findAllById(anyList());
		verify(specializationRepository).save(entity);
	}

	@Test
	public void shouldReturnSpecializationWhenInputIdIsValid() {
		Specialization entity = new Specialization();
		entity.setId(1L);
		entity.setName("Cardiologist");
		entity.setDescription("Heart specialist");

		SpecializationResponseDto responseDto = new SpecializationResponseDto(1L, entity.getName(),
				entity.getDescription(), new ArrayList<>());

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
		SpecializationCreateDto dto = new SpecializationCreateDto("Cardiologist2", "Heart specialist2", List.of(1));

		ProvidedService service = new ProvidedService();
		service.setId(1L);

		Specialization entity = new Specialization();
		entity.setId(1L);
		entity.setName("Cardiologist2");
		entity.setDescription("Heart specialist2");
		ArrayList<ProvidedService> services = new ArrayList<>();
		services.add(service);
		entity.setServices(services);

		when(specializationRepository.findById(1L)).thenReturn(Optional.of(entity));
		when(providedServiceRepository.findAllById(anyList())).thenReturn(List.of(service));

		long result = specializationService.update(1L, dto);

		assertEquals(Long.valueOf(1L), result);

		verify(specializationRepository).findById(any(Long.class));
		verify(providedServiceRepository).findAllById((anyList()));
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

		SpecializationResponseDto responseDto = new SpecializationResponseDto(1L, entity.getName(),
				entity.getDescription(), new ArrayList<>());

		SpecializationResponseDto responseDto2 = new SpecializationResponseDto(2L, entity2.getName(),
				entity2.getDescription(), new ArrayList<>());

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
		SpecializationCreateDto dto = new SpecializationCreateDto("Cardiologist", "Heart specialist", List.of(1));
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

		ProvidedService service = new ProvidedService();
		service.setId(1L);

		ArrayList<ProvidedService> services = new ArrayList<>();
		services.add(service);

		Specialization entity2 = new Specialization();
		entity2.setServices(services);

		Specialization entity = new Specialization();
		entity.setId(1L);
		entity.setName("Cardiologist2");
		entity.setDescription("Heart specialist2");
		entity.setServices(services);

		ArrayList<Specialization> specList = new ArrayList<>();
		specList.add(entity);
		specList.add(entity2);
		service.setSpecializations(specList);

		when(specializationRepository.findById(id)).thenReturn(Optional.of(entity));

		specializationService.deleteById(id);

		verify(specializationRepository).findById(id);
		verify(specializationRepository).delete(entity);
		verifyNoMoreInteractions(specializationRepository);
	}

	@Test
	void shouldReturnSpecializationsWhenAllIdsExist() {
		Set<Long> ids = Set.of(1L, 2L);

		Specialization s1 = new Specialization();
		s1.setId(1L);
		Specialization s2 = new Specialization();
		s2.setId(2L);

		List<Specialization> foundEntities = List.of(s1, s2);

		when(specializationRepository.findAllById(ids)).thenReturn(foundEntities);
		List<Specialization> result = specializationService.findAllByIds(ids);

		assertThat(result).hasSize(2);
		verify(specializationRepository).findAllById(ids);
	}

}
