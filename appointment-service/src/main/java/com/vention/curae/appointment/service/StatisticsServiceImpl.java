package com.vention.curae.appointment.service;

import com.vention.curae.appointment.dto.StatusStatisticDto;
import com.vention.curae.appointment.repository.AppointmentRepository;
import com.vention.curae.appointment.repository.TestRepository;
import com.vention.curae.appointment.repository.VisitRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

	private final AppointmentRepository appointmentRepository;

	private final VisitRepository visitRepository;

	private final TestRepository testRepository;

	@Override
	@Transactional(readOnly = true)
	public List<StatusStatisticDto> appointmentStatusStatistics() {
		return appointmentRepository.countByStatus()
			.stream()
			.map(row -> new StatusStatisticDto(String.valueOf(row[0]), ((Number) row[1]).longValue()))
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<StatusStatisticDto> visitStatisticsByClinic() {
		return visitRepository.countByClinic()
			.stream()
			.map(row -> new StatusStatisticDto(String.valueOf(row[0]), ((Number) row[1]).longValue()))
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<StatusStatisticDto> visitStatisticsByDoctor() {
		return visitRepository.countByDoctor()
			.stream()
			.map(row -> new StatusStatisticDto(String.valueOf(row[0]), ((Number) row[1]).longValue()))
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<StatusStatisticDto> testStatisticsByClinic() {
		return testRepository.countByClinic()
			.stream()
			.map(row -> new StatusStatisticDto(String.valueOf(row[0]), ((Number) row[1]).longValue()))
			.toList();
	}

}
