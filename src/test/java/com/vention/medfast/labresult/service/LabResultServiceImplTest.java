package com.vention.medfast.labresult.service;

import com.vention.medfast.labresult.repository.LabResultRepository;
import com.vention.medfast.statistics.dto.response.LabResultStatusCounts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LabResultServiceImplTest {

	@Mock
	private LabResultRepository labResultRepository;

	@InjectMocks
	private LabResultServiceImpl labResultService;

	@Test
	void shouldReturnStatusStatistics() {
		LocalDate startDate = LocalDate.of(2026, 1, 1);
		LocalDate endDate = LocalDate.of(2026, 1, 31);

		LabResultStatusCounts expectedCounts = new LabResultStatusCounts(2L, 5L, 1L);

		when(labResultRepository.getStatusCounts(startDate, endDate)).thenReturn(expectedCounts);

		LabResultStatusCounts result = labResultService.getStatusStatistics(startDate, endDate);

		assertThat(result).isNotNull();
		assertThat(result.processing()).isEqualTo(2L);
		assertThat(result.ready()).isEqualTo(5L);
		assertThat(result.failed()).isEqualTo(1L);

		verify(labResultRepository).getStatusCounts(startDate, endDate);
	}

}
