package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.StatusStatisticDto;
import com.vention.medfast.appointment.dto.VisitCreateDto;
import com.vention.medfast.appointment.dto.VisitListItemDto;
import com.vention.medfast.appointment.event.AppointmentEventPublisher;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.model.Visit;
import com.vention.medfast.appointment.model.VisitType;
import com.vention.medfast.appointment.repository.AppointmentRepository;
import com.vention.medfast.appointment.repository.VisitRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

	private final VisitRepository visitRepository;

	private final AppointmentRepository appointmentRepository;

	private final AppointmentEventPublisher eventPublisher;

	@Override
	@Transactional
	public Long create(Long patientId, VisitCreateDto dto) {
		Appointment appointment = new Appointment();
		appointment.setPatientId(patientId);
		appointment.setType(AppointmentType.VISIT);
		appointment.setStatus(AppointmentStatus.COMPLETED);
		appointment.setScheduledDate(LocalDate.now());
		appointment.setScheduledTime(LocalTime.now());
		Appointment savedAppointment = appointmentRepository.save(appointment);

		Visit visit = new Visit();
		visit.setAppointmentId(savedAppointment.getId());
		visit.setDiagnosis(dto.diagnosis());
		visit.setSymptoms(dto.symptoms());
		visit.setMedicalReport(dto.medicalReport());
		visit.setDoctorId(dto.doctorId());
		visit.setServiceId(dto.serviceId());
		visit.setClinicId(dto.clinicId());
		visit.setDoctorFirstName(dto.doctorFirstName());
		visit.setDoctorLastName(dto.doctorLastName());
		visit.setClinicName(dto.clinicName());
		visit.setCity(dto.city());
		Visit saved = visitRepository.save(visit);
		eventPublisher.appointmentCreated(savedAppointment, dto.doctorId(), dto.serviceId());
		return saved.getAppointmentId();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<VisitListItemDto> findAllForPatient(Long patientId, VisitType type, Pageable pageable) {
		List<AppointmentStatus> statuses = type == VisitType.UPCOMING
				? List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.SCHEDULED_CONFIRMED,
						AppointmentStatus.IN_CONSULTATION)
				: List.of(AppointmentStatus.COMPLETED, AppointmentStatus.CANCELLED, AppointmentStatus.MISSED);
		Page<Appointment> appointments = appointmentRepository
			.findByPatientIdAndTypeAndStatusInOrderByScheduledDateDescScheduledTimeDesc(patientId,
					AppointmentType.VISIT, statuses, pageable);
		return appointments.map(appointment -> {
			Visit visit = visitRepository.findByAppointmentId(appointment.getId()).orElse(new Visit());
			return new VisitListItemDto(visit.getDoctorFirstName(), visit.getDoctorLastName(), visit.getClinicName(),
					visit.getCity(), appointment.getScheduledDate(), appointment.getScheduledTime(),
					appointment.getStatus());
		});
	}

	@Override
	@Transactional(readOnly = true)
	public List<StatusStatisticDto> getStatusStatisticsByClinic() {
		return visitRepository.countByClinic()
			.stream()
			.map(row -> new StatusStatisticDto(String.valueOf(row[0]), ((Number) row[1]).longValue()))
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<StatusStatisticDto> getStatusStatisticsByDoctor() {
		return visitRepository.countByDoctor()
			.stream()
			.map(row -> new StatusStatisticDto(String.valueOf(row[0]), ((Number) row[1]).longValue()))
			.toList();
	}

}
