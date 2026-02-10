package com.vention.medfast.statistics.service;

import com.vention.medfast.statistics.dto.request.StatisticsPeriodRequestDto;
import com.vention.medfast.statistics.dto.response.ClinicStatisticsResponseDto;
import com.vention.medfast.statistics.dto.response.DoctorStatisticsResponseDto;
import com.vention.medfast.statistics.dto.response.StatisticsResponseDto;
import java.time.LocalDate;

public interface StatisticsService {

	StatisticsResponseDto getOverPeriod(StatisticsPeriodRequestDto dto);

	ClinicStatisticsResponseDto getClinicStatOverPeriod(Long clinicId, LocalDate startDate, LocalDate endDate);

	DoctorStatisticsResponseDto getDoctorStatsOverPeriod(Long doctorId, LocalDate start, LocalDate end);

}
