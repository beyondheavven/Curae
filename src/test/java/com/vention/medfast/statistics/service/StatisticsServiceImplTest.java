package com.vention.medfast.statistics.service;

import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.service.AppointmentService;
import com.vention.medfast.appointment.service.OnlineConsultationService;
import com.vention.medfast.appointment.service.TestService;
import com.vention.medfast.appointment.service.VisitService;
import com.vention.medfast.labresult.service.LabResultService;
import com.vention.medfast.statistics.dto.request.StatisticsPeriodRequestDto;
import com.vention.medfast.statistics.dto.response.AppointmentStatusCounts;
import com.vention.medfast.statistics.dto.response.ClinicStatisticsResponseDto;
import com.vention.medfast.statistics.dto.response.DoctorStatisticsResponseDto;
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

	@Mock
	private VisitService visitService;

	@Mock
	private TestService testService;

	@Mock
	private OnlineConsultationService onlineConsultationService;

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

	@Test
	void shouldReturnClinicStatsOverPeriod() {
		Long clinicId = 5L;
		LocalDate startDate = LocalDate.of(2026, 1, 1);
		LocalDate endDate = LocalDate.of(2026, 1, 24);

		AppointmentStatusCounts visitCounts = new AppointmentStatusCounts(2L, 1L, 0L, 1L, 0L);
		AppointmentStatusCounts testCounts = new AppointmentStatusCounts(3L, 2L, 0L, 1L, 0L);
		AppointmentStatusCounts onlineCounts = new AppointmentStatusCounts(4L, 1L, 1L, 2L, 0L);
		LabResultStatusCounts labResultCounts = new LabResultStatusCounts(1L, 2L, 0L);

		when(visitService.getStatusStatisticsByClinic(clinicId, startDate, endDate)).thenReturn(visitCounts);
		when(testService.getStatusStatisticsByClinic(clinicId, startDate, endDate)).thenReturn(testCounts);
		when(onlineConsultationService.getStatusStatisticsByClinic(clinicId, startDate, endDate))
			.thenReturn(onlineCounts);
		when(labResultService.getStatusCountsByClinic(clinicId, startDate, endDate)).thenReturn(labResultCounts);

		ClinicStatisticsResponseDto result = statisticsService.getClinicStatOverPeriod(clinicId, startDate, endDate);

		assertThat(result.clinicId()).isEqualTo(clinicId);
		assertThat(result.period().startDate()).isEqualTo(startDate);
		assertThat(result.period().endDate()).isEqualTo(endDate);
		assertThat(result.timestamp()).isNotNull();

		assertThat(result.visits().stats().total()).isEqualTo(2L);
		assertThat(result.onlineConsultations().stats().total()).isEqualTo(4L);
		assertThat(result.lab().tests().total()).isEqualTo(3L);
		assertThat(result.lab().results().ready()).isEqualTo(2L);

		verify(visitService).getStatusStatisticsByClinic(clinicId, startDate, endDate);
		verify(testService).getStatusStatisticsByClinic(clinicId, startDate, endDate);
		verify(onlineConsultationService).getStatusStatisticsByClinic(clinicId, startDate, endDate);
		verify(labResultService).getStatusCountsByClinic(clinicId, startDate, endDate);
	}

	@Test
	void shouldReturnDoctorStatsOverPeriod() {
		Long doctorId = 7L;
		LocalDate startDate = LocalDate.of(2026, 1, 1);
		LocalDate endDate = LocalDate.of(2026, 1, 24);

		AppointmentStatusCounts visitCounts = new AppointmentStatusCounts(5L, 2L, 1L, 2L, 0L);
		AppointmentStatusCounts onlineCounts = new AppointmentStatusCounts(3L, 1L, 0L, 2L, 0L);

		when(visitService.getStatusStatisticsByDoctor(doctorId, startDate, endDate)).thenReturn(visitCounts);
		when(onlineConsultationService.getStatusStatisticsByDoctor(doctorId, startDate, endDate))
			.thenReturn(onlineCounts);

		DoctorStatisticsResponseDto result = statisticsService.getDoctorStatsOverPeriod(doctorId, startDate, endDate);

		assertThat(result.doctorId()).isEqualTo(doctorId);
		assertThat(result.period().startDate()).isEqualTo(startDate);
		assertThat(result.period().endDate()).isEqualTo(endDate);
		assertThat(result.timestamp()).isNotNull();

		assertThat(result.visits().stats().total()).isEqualTo(5L);
		assertThat(result.onlineConsultations().stats().total()).isEqualTo(3L);
		assertThat(result.onlineConsultations().stats().completed()).isEqualTo(2L);

		verify(visitService).getStatusStatisticsByDoctor(doctorId, startDate, endDate);
		verify(onlineConsultationService).getStatusStatisticsByDoctor(doctorId, startDate, endDate);
	}

}
