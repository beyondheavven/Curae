package com.vention.medfast.appointment.service;

import com.vention.medfast.api.exception.AppointmentException;
import com.vention.medfast.appointment.dto.AppointmentCreateDto;
import com.vention.medfast.appointment.dto.FirstAppointmentCreateDto;
import com.vention.medfast.appointment.mapper.AppointmentMapper;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.Status;
import com.vention.medfast.appointment.referral.model.Referral;
import com.vention.medfast.appointment.referral.service.ReferralService;
import com.vention.medfast.appointment.repository.AppointmentRepository;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.service.DoctorService;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ReferralService referralService;
    private final DoctorService doctorService;
    private final AppointmentMapper appointmentMapper;
    private final PatientService patientService;

    @Override
    @Transactional
    public Long createFirst(FirstAppointmentCreateDto dto) {
        Patient patient = patientService.getCurrentPatient();
        Doctor doctor = doctorService.findDoctorById(dto.doctorId());
        Appointment appointment =
            appointmentMapper.toFirstAppointment(dto, doctor, patient);
        appointment.setStatus(Status.SCHEDULED_CONFIRMED);
        return appointmentRepository.save(appointment).getId();
    }

    @Override
    @Transactional
    public Long createDraft(AppointmentCreateDto dto) {
        Appointment appointment;
        if (dto.referralId() != null) {
            Referral referral = referralService.getActiveById(dto.referralId());
            appointment = appointmentMapper.toAppointment(dto, referral.getIssuedBy(), referral.getPatient(), referral);
            appointment.setType(referral.getAppointmentType());
            appointment.setStatus(Status.SCHEDULED);
        } else {
            Patient patient = patientService.getCurrentPatient();
            Doctor doctor = doctorService.findDoctorById(dto.doctorId());
            appointment = appointmentMapper.toAppointment(dto, doctor, patient, null);
            appointment.setStatus(Status.SCHEDULED_CONFIRMED);
        }
        return appointmentRepository.save(appointment).getId();
    }

    @Override
    @Transactional
    public void confirm(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
        if (appointment.getStatus() != Status.SCHEDULED) {
            throw new AppointmentException("Only draft appointment can be confirmed");
        }
        appointment.setStatus(Status.SCHEDULED_CONFIRMED);
        if (appointment.getReferral() != null) {
            referralService.markUsed(appointment.getReferral());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Appointment getConfirmed(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
        if (appointment.getStatus() != Status.SCHEDULED_CONFIRMED) {
            throw new AppointmentException("Appointment is not confirmed");
        }
        return appointment;
    }
}
