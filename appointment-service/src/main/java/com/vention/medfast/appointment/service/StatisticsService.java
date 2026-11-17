package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.StatusStatisticDto;
import java.util.List;

public interface StatisticsService {

	List<StatusStatisticDto> appointmentStatusStatistics();

	List<StatusStatisticDto> visitStatisticsByClinic();

	List<StatusStatisticDto> visitStatisticsByDoctor();

	List<StatusStatisticDto> testStatisticsByClinic();

}
