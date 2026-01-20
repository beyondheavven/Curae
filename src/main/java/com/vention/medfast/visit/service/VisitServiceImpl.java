package com.vention.medfast.visit.service;

import com.vention.medfast.api.exception.AppointmentException;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.service.AppointmentService;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.visit.dto.VisitCreateDto;
import com.vention.medfast.visit.dto.VisitResponseDto;
import com.vention.medfast.visit.mapper.VisitMapper;
import com.vention.medfast.visit.model.Visit;
import com.vention.medfast.visit.model.VisitType;
import com.vention.medfast.visit.repository.VisitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final VisitMapper visitMapper;
    private final AppointmentService appointmentService;
    private final PatientService patientService;

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
        appointment.setStatus(AppointmentStatus.COMPLETED);
        return saved.getId();
    }

    @Override
    public Page<VisitResponseDto> findAllForPatient(VisitType type, Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Patient patient = patientService.getPatientByEmail(email);
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalTime time = now.toLocalTime();
        Page<Visit> visits = switch (type) {
            case PAST -> visitRepository.findPastByPatient(patient.getId(), today, time, pageable);
            case UPCOMING -> visitRepository.findUpcomingByPatient(patient.getId(), today, time, pageable);
        };
        return visits.map(visitMapper::toVisitResponseDto);
    }
}
