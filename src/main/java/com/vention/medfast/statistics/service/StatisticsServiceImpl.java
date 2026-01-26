package com.vention.medfast.statistics.service;

import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.service.AppointmentService;
import com.vention.medfast.labresult.service.LabResultService;
import com.vention.medfast.statistics.dto.request.StatisticsPeriodRequestDto;
import com.vention.medfast.statistics.dto.response.AppointmentStatisticsDto;
import com.vention.medfast.statistics.dto.response.AppointmentStatusCounts;
import com.vention.medfast.statistics.dto.response.LabResultStatusCounts;
import com.vention.medfast.statistics.dto.response.LabTestStatisticsDto;
import com.vention.medfast.statistics.dto.response.Period;
import com.vention.medfast.statistics.dto.response.StatisticsResponseDto;
import com.vention.medfast.statistics.dto.response.VisitStatisticsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

	private final AppointmentService appointmentService;

	private final LabResultService labResultService;

	@Override
	public StatisticsResponseDto getOverPeriod(StatisticsPeriodRequestDto dto) {
		LocalDate startDate = dto.startDate();
		LocalDate endDate = dto.endDate();

		AppointmentStatusCounts appointmentsCounts = appointmentService.getAllStatusStatistics(startDate, endDate);
		AppointmentStatusCounts visitCounts = appointmentService.getStatusStatisticsByType(startDate, endDate,
				AppointmentType.VISIT);

		AppointmentStatusCounts labTestCounts = appointmentService.getStatusStatisticsByType(startDate, endDate,
				AppointmentType.TEST);
		LabResultStatusCounts labResultCounts = labResultService.getStatusStatistics(startDate, endDate);

		return StatisticsResponseDto.builder()
			.period(new Period(startDate, endDate))
			.timestamp(LocalDateTime.now())
			.appointments(new AppointmentStatisticsDto(appointmentsCounts))
			.visits(new VisitStatisticsDto(visitCounts))
			.labResults(new LabTestStatisticsDto(labTestCounts, labResultCounts))
			.build();
	}

}
