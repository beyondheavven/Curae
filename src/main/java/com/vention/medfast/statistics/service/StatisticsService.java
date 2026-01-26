package com.vention.medfast.statistics.service;

import com.vention.medfast.statistics.dto.request.StatisticsPeriodRequestDto;
import com.vention.medfast.statistics.dto.response.StatisticsResponseDto;

public interface StatisticsService {

	StatisticsResponseDto getOverPeriod(StatisticsPeriodRequestDto dto);

}
