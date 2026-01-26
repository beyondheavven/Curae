package com.vention.medfast.statistics.service;

import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.service.AppointmentService;
import com.vention.medfast.labresult.service.LabResultService;
import com.vention.medfast.statistics.dto.request.StatisticsPeriodRequestDto;
import com.vention.medfast.statistics.dto.response.AppointmentStatusCounts;
import com.vention.medfast.statistics.dto.response.LabResultStatusCounts;
import com.vention.medfast.statistics.dto.response.StatisticsResponseDto;
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
class StatisticsServiceImplTest {

	@Mock
	private AppointmentService appointmentService;

	@Mock
	private LabResultService labResultService;

	@InjectMocks
	private StatisticsServiceImpl statisticsService;

	@Test
	void shouldGetCorrectStatisticsResponseWhenInputIsValid() {
		LocalDate startDate = LocalDate.of(2026, 1, 1);
		LocalDate endDate = LocalDate.of(2026, 1, 24);
		StatisticsPeriodRequestDto requestDto = new StatisticsPeriodRequestDto(startDate, endDate);

		AppointmentStatusCounts globalCounts = new AppointmentStatusCounts(3L, 1L, 0L, 2L, 0L);
		AppointmentStatusCounts visitCounts = new AppointmentStatusCounts(2L, 1L, 0L, 1L, 0L);
		AppointmentStatusCounts labTestCounts = new AppointmentStatusCounts(1L, 0L, 0L, 1L, 0L);
		LabResultStatusCounts labResultCounts = new LabResultStatusCounts(1L, 3L, 0L);

		when(appointmentService.getAllStatusStatistics(startDate, endDate)).thenReturn(globalCounts);
		when(appointmentService.getStatusStatisticsByType(startDate, endDate, AppointmentType.VISIT))
			.thenReturn(visitCounts);
		when(appointmentService.getStatusStatisticsByType(startDate, endDate, AppointmentType.TEST))
			.thenReturn(labTestCounts);
		when(labResultService.getStatusStatistics(startDate, endDate)).thenReturn(labResultCounts);

		StatisticsResponseDto result = statisticsService.getOverPeriod(requestDto);

		assertThat(result).isNotNull();

		assertThat(result.period().startDate()).isEqualTo(startDate);
		assertThat(result.period().endDate()).isEqualTo(endDate);
		assertThat(result.timestamp()).isNotNull();

		assertThat(result.appointments().stats().total()).isEqualTo(3L);
		assertThat(result.appointments().stats().completed()).isEqualTo(2L);

		assertThat(result.visits().stats().total()).isEqualTo(2L);
		assertThat(result.visits().stats().scheduled()).isEqualTo(1L);

		assertThat(result.labResults().stats().total()).isEqualTo(1L);
		assertThat(result.labResults().results().ready()).isEqualTo(3L);

		verify(appointmentService).getAllStatusStatistics(startDate, endDate);
		verify(appointmentService).getStatusStatisticsByType(startDate, endDate, AppointmentType.VISIT);
		verify(appointmentService).getStatusStatisticsByType(startDate, endDate, AppointmentType.TEST);
		verify(labResultService).getStatusStatistics(startDate, endDate);
	}

}
