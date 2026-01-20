package com.vention.medfast.country.service;

import com.vention.medfast.country.dto.CountryResponseDto;
import com.vention.medfast.country.mapper.CountryMapper;
import com.vention.medfast.country.model.Country;
import com.vention.medfast.country.repository.CountryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryServiceImplTest {

	@Mock
	private CountryRepository countryRepository;

	@Mock
	private CountryMapper countryMapper;

	@InjectMocks
	private CountryServiceImpl countryService;

	@Test
	void shouldReturnPageOfCountryResponseDtos() {
		Pageable pageable = PageRequest.of(0, 10);

		Country country = new Country();
		country.setId(1L);
		country.setName("United States");

		CountryResponseDto dto = new CountryResponseDto(1L, "US", "United States", "American", "\\d{5}", "12345");

		Page<Country> countryPage = new PageImpl<>(List.of(country));

		when(countryRepository.findAll(pageable)).thenReturn(countryPage);
		when(countryMapper.toDto(country)).thenReturn(dto);

		Page<CountryResponseDto> result = countryService.getAll(pageable);

		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().getFirst()).isEqualTo(dto);

		verify(countryRepository).findAll(pageable);
		verify(countryMapper).toDto(country);
	}

	@Test
	void shouldReturnEmptyPageWhenNoCountriesExist() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<Country> emptyPage = new PageImpl<>(Collections.emptyList());

		when(countryRepository.findAll(pageable)).thenReturn(emptyPage);

		Page<CountryResponseDto> result = countryService.getAll(pageable);

		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isZero();
		assertThat(result.getContent()).isEmpty();

		verify(countryRepository).findAll(pageable);
		verifyNoInteractions(countryMapper);
	}

}
