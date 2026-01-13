package com.vention.medfast.service.service;

import com.vention.medfast.service.dto.ProvidedServiceCreateDto;
import com.vention.medfast.service.dto.ProvidedServiceResponseDto;
import com.vention.medfast.service.mapper.ProvidedServiceMapper;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.service.repository.ProvidedServiceRepository;
import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.specialization.repository.SpecializationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProvidedServiceServiceImplTest {

	@Mock
	private ProvidedServiceMapper providedServiceMapper;

	@Mock
	private SpecializationRepository specializationRepository;

	@Mock
	private ProvidedServiceRepository providedServiceRepository;

	@InjectMocks
	private ProvidedServiceServiceImpl providedServiceService;

	@Test
	public void shouldCreateServiceWhenInputIsValid() {
		ProvidedServiceCreateDto dto = new ProvidedServiceCreateDto("Test name", "Test description", 10.5, 10,
				List.of(1));

		Specialization spec = new Specialization();
		spec.setId(1L);

		ProvidedService service = new ProvidedService();
		service.setId(1L);
		service.setName(dto.name());
		service.setDescription(dto.description());
		service.setPrice(BigDecimal.valueOf(dto.price()));
		service.setDuration(dto.duration());

		when(providedServiceMapper.toEntity(dto)).thenReturn(service);
		when(specializationRepository.findAllById(anyList())).thenReturn(List.of(spec));
		when(providedServiceRepository.save(service)).thenReturn(service);

		long resultId = providedServiceService.create(dto);

		assertEquals(1L, resultId);

		verify(providedServiceMapper).toEntity(dto);
		verify(providedServiceRepository).save(service);
		verify(specializationRepository).findAllById(List.of(1L));
	}

	@Test
	public void shouldReturnServiceWhenInputIdIsValid() {
		ProvidedService service = new ProvidedService();
		service.setId(1L);
		service.setName("Test name");
		service.setDescription("Test description");
		service.setPrice(BigDecimal.valueOf(10.5));
		service.setDuration(10);

		ProvidedServiceResponseDto responseDto = new ProvidedServiceResponseDto(1L, service.getName(),
				service.getDescription(), 10.5, 10, new ArrayList<>());

		when(providedServiceMapper.toDto(service)).thenReturn(responseDto);
		when(providedServiceRepository.findById(1L)).thenReturn(Optional.of(service));

		ProvidedServiceResponseDto result = providedServiceService.getById(1L);

		assertEquals(Long.valueOf(1L), result.id());
		assertEquals("Test name", result.name());
		assertEquals("Test description", result.description());

		verify(providedServiceMapper).toDto(service);
		verify(providedServiceRepository).findById(any(Long.class));
	}

	@Test
	public void shouldUpdateServiceWhenInputIsValid() {
		ProvidedServiceCreateDto dto = new ProvidedServiceCreateDto("New name", "New description", 10.5, 10,
				List.of(1));

		Specialization spec = new Specialization();
		spec.setId(1L);

		ProvidedService service = new ProvidedService();
		service.setId(1L);
		service.setName("Old name");
		service.setDescription("Old description");
		service.setPrice(BigDecimal.valueOf(10.5));
		service.setDuration(10);
		ArrayList<Specialization> specializations = new ArrayList<>();
		specializations.add(spec);
		service.setSpecializations(specializations);
		spec.setServices(List.of(service));

		when(specializationRepository.findAllById(anyList())).thenReturn(List.of(spec));
		when(providedServiceRepository.findById(1L)).thenReturn(Optional.of(service));

		long result = providedServiceService.update(1L, dto);

		assertEquals(Long.valueOf(1L), result);

		verify(specializationRepository).findAllById(anyList());
		verify(providedServiceRepository).findById(any(Long.class));
	}

	@Test
	public void shouldReturnServicesListWhenInputIsValid() {
		ProvidedService service1 = new ProvidedService();
		service1.setId(1L);
		service1.setName("Old name");
		service1.setDescription("Old description");
		service1.setPrice(BigDecimal.valueOf(10.6));
		service1.setDuration(10);

		ProvidedService service2 = new ProvidedService();
		service2.setId(2L);
		service2.setName("Old name");
		service2.setDescription("Old description");
		service2.setPrice(BigDecimal.valueOf(10.6));
		service2.setDuration(10);

		List<ProvidedService> entityResultList = List.of(service1, service2);

		ProvidedServiceResponseDto responseDto1 = new ProvidedServiceResponseDto(1L, service1.getName(),
				service1.getDescription(), service1.getPrice().doubleValue(), service1.getDuration(),
				new ArrayList<>());

		ProvidedServiceResponseDto responseDto2 = new ProvidedServiceResponseDto(2L, service1.getName(),
				service1.getDescription(), service1.getPrice().doubleValue(), service1.getDuration(),
				new ArrayList<>());

		when(providedServiceMapper.toDto(service1)).thenReturn(responseDto1);
		when(providedServiceMapper.toDto(service2)).thenReturn(responseDto2);
		when(providedServiceRepository.findAll()).thenReturn(entityResultList);

		List<ProvidedServiceResponseDto> result = providedServiceService.getAll();

		assertEquals(2, result.size());

		assertEquals(Long.valueOf(1L), result.getFirst().id());
		assertEquals(Long.valueOf(2L), result.get(1).id());

		verify(providedServiceRepository).findAll();
		verify(providedServiceMapper).toDto(service1);
		verify(providedServiceMapper).toDto(service2);
	}

	@Test
	public void shouldThrowExceptionWhenGetServiceNotFoundById() {
		Long id = 10L;
		when(providedServiceRepository.findById(id)).thenReturn(Optional.empty());
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
				() -> providedServiceService.getById(id));

		assert (exception.getMessage().contains("Service not found"));
		verify(providedServiceRepository).findById(id);
		verifyNoMoreInteractions(providedServiceRepository);
		verifyNoInteractions(providedServiceMapper);
	}

	@Test
	public void shouldThrowExceptionWhenDeleteServiceNotFoundById() {
		Long id = 10L;
		when(providedServiceRepository.findById(id)).thenReturn(Optional.empty());
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
				() -> providedServiceService.deleteById(id));

		assert (exception.getMessage().contains("Service not found"));
		verify(providedServiceRepository).findById(id);
		verifyNoMoreInteractions(providedServiceRepository);
		verifyNoInteractions(providedServiceMapper);
	}

	@Test
	public void shouldThrowExceptionWhenUpdateServiceNotFound() {
		Long id = 10L;
		ProvidedServiceCreateDto dto = new ProvidedServiceCreateDto("Test name", "Test description", 10.5, 10,
				List.of(1));
		when(providedServiceRepository.findById(id)).thenReturn(Optional.empty());

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
				() -> providedServiceService.update(id, dto));

		assert (exception.getMessage().contains("Service not found"));

		verify(providedServiceRepository).findById(id);
		verifyNoMoreInteractions(providedServiceRepository);
		verifyNoInteractions(providedServiceMapper);
	}

	@Test
	public void shouldDeleteServiceWhenInputIsValid() {
		Long id = 1L;

		ProvidedService service = new ProvidedService();
		service.setId(2L);
		service.setName("Old name");
		service.setDescription("Old description");
		service.setPrice(BigDecimal.valueOf(10.6));
		service.setDuration(10);

		ProvidedService service2 = new ProvidedService();
		service.setId(2L);

		Specialization spec = new Specialization();
		spec.setId(1L);

		List<Specialization> specializations = new ArrayList<>();
		specializations.add(spec);
		List<ProvidedService> services = new ArrayList<>();
		services.add(service);
		services.add(service2);

		service.setSpecializations(specializations);
		spec.setServices(services);

		when(providedServiceRepository.findById(id)).thenReturn(Optional.of(service));

		providedServiceService.deleteById(id);

		verify(providedServiceRepository).findById(id);
		verifyNoInteractions(specializationRepository);
		verify(providedServiceRepository).delete(service);
		verifyNoMoreInteractions(providedServiceRepository);
	}

	@Test
	public void shouldReturnPagedServicesWhenSearchQueryIsValid() {
		String query = "Checkup";
		Pageable pageable = PageRequest.of(0, 10);

		ProvidedService service = new ProvidedService();
		service.setId(1L);
		service.setName("General Checkup");
		service.setPrice(BigDecimal.valueOf(50.0));

		ProvidedServiceResponseDto responseDto = new ProvidedServiceResponseDto(1L, "General Checkup", "Desc", 50.0, 30,
				new ArrayList<>());

		List<ProvidedService> entityList = List.of(service);
		Page<ProvidedService> entityPage = new PageImpl<>(entityList, pageable, 1);

		when(providedServiceRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(entityPage);
		when(providedServiceMapper.toDto(service)).thenReturn(responseDto);

		Page<ProvidedServiceResponseDto> result = providedServiceService.search(query, pageable);

		assertEquals(1, result.getTotalElements());
		assertEquals("General Checkup", result.getContent().get(0).name());

		verify(providedServiceRepository).findAll(any(Specification.class), eq(pageable));
		verify(providedServiceMapper).toDto(service);
	}

	@Test
	public void shouldReturnEmptyPageWhenSearchReturnsNoResults() {
		String query = "Unknown Service";
		Pageable pageable = PageRequest.of(0, 10);

		Page<ProvidedService> emptyPage = new PageImpl<>(List.of(), pageable, 0);

		when(providedServiceRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

		Page<ProvidedServiceResponseDto> result = providedServiceService.search(query, pageable);

		assertEquals(0, result.getTotalElements());
		assertEquals(0, result.getContent().size());

		verify(providedServiceRepository).findAll(any(Specification.class), eq(pageable));
		verifyNoInteractions(providedServiceMapper);
	}

}
