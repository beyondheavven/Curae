package com.vention.medfast.statistics.service;

import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.service.AppointmentService;
import com.vention.medfast.appointment.service.OnlineConsultationService;
import com.vention.medfast.appointment.service.TestService;
import com.vention.medfast.appointment.service.VisitService;
import com.vention.medfast.labresult.service.LabResultService;
import com.vention.medfast.statistics.dto.request.StatisticsPeriodRequestDto;
import com.vention.medfast.statistics.dto.response.AppointmentStatisticsDto;
import com.vention.medfast.statistics.dto.response.AppointmentStatusCounts;
import com.vention.medfast.statistics.dto.response.ClinicStatisticsResponseDto;
import com.vention.medfast.statistics.dto.response.DoctorStatisticsResponseDto;
import com.vention.medfast.statistics.dto.response.LabResultStatusCounts;
import com.vention.medfast.statistics.dto.response.LabStatisticsDto;
import com.vention.medfast.statistics.dto.response.LabTestStatisticsDto;
import com.vention.medfast.statistics.dto.response.OnlineConsultationStatisticsDto;
import com.vention.medfast.statistics.dto.response.Period;
import com.vention.medfast.statistics.dto.response.StatisticsResponseDto;
import com.vention.medfast.statistics.dto.response.VisitStatisticsDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

	private final AppointmentService appointmentService;

	private final LabResultService labResultService;

	private final VisitService visitService;

	private final TestService testService;

	private final OnlineConsultationService onlineConsultationService;

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

	@Override
	public ClinicStatisticsResponseDto getClinicStatOverPeriod(Long clinicId, LocalDate startDate, LocalDate endDate) {

		AppointmentStatusCounts visitCounts = visitService.getStatusStatisticsByClinic(clinicId, startDate, endDate);

		AppointmentStatusCounts labTestCounts = testService.getStatusStatisticsByClinic(clinicId, startDate, endDate);

		AppointmentStatusCounts onlineCounts = onlineConsultationService.getStatusStatisticsByClinic(clinicId,
				startDate, endDate);

		LabResultStatusCounts labResultCounts = labResultService.getStatusCountsByClinic(clinicId, startDate, endDate);

		return ClinicStatisticsResponseDto.builder()
			.clinicId(clinicId)
			.period(new Period(startDate, endDate))
			.timestamp(LocalDateTime.now())
			.visits(new VisitStatisticsDto(visitCounts))
			.onlineConsultations(new OnlineConsultationStatisticsDto(onlineCounts))
			.lab(new LabStatisticsDto(labTestCounts, labResultCounts))
			.build();
	}

	@Override
	public DoctorStatisticsResponseDto getDoctorStatsOverPeriod(Long doctorId, LocalDate start, LocalDate end) {
		AppointmentStatusCounts visitCounts = visitService.getStatusStatisticsByDoctor(doctorId, start, end);
		AppointmentStatusCounts onlineCounts = onlineConsultationService.getStatusStatisticsByDoctor(doctorId, start,
				end);

		return DoctorStatisticsResponseDto.builder()
			.doctorId(doctorId)
			.period(new Period(start, end))
			.timestamp(LocalDateTime.now())
			.visits(new VisitStatisticsDto(visitCounts))
			.onlineConsultations(new OnlineConsultationStatisticsDto(onlineCounts))
			.build();
	}

}
