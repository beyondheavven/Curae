package com.vention.medfast.visit.service;

import com.vention.medfast.api.exception.AppointmentException;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.model.Status;
import com.vention.medfast.appointment.service.AppointmentService;
import com.vention.medfast.visit.dto.VisitCreateDto;
import com.vention.medfast.visit.mapper.VisitMapper;
import com.vention.medfast.visit.model.Visit;
import com.vention.medfast.visit.repository.VisitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

	private final VisitRepository visitRepository;

	private final VisitMapper visitMapper;

    private final AppointmentService appointmentService;

	@Override
	@Transactional
	public long create(VisitCreateDto dto) {
        Appointment appointment = appointmentService.getConfirmed(dto.appointmentId());
        if (appointment.getType() != AppointmentType.VISIT) {
            throw new AppointmentException("Appointment is not a visit");
        }
        if (visitRepository.existsById(appointment.getId())) {
            throw new AppointmentException("Visit already exists for this appointment");
        }
        Visit visit = visitMapper.toEntity(dto, appointment);
        Visit saved = visitRepository.save(visit);
        appointment.setStatus(Status.COMPLETED);
        return saved.getId();
	}

}
