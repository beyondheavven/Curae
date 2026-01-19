package com.vention.medfast.appointment.service;

import com.vention.medfast.api.exception.AppointmentException;
import com.vention.medfast.appointment.dto.AppointmentCreateDto;
import com.vention.medfast.appointment.mapper.AppointmentMapper;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.model.Status;
import com.vention.medfast.appointment.referral.model.Referral;
import com.vention.medfast.appointment.referral.model.ReferralStatus;
import com.vention.medfast.appointment.referral.service.ReferralService;
import com.vention.medfast.appointment.repository.AppointmentRepository;
import com.vention.medfast.doctor.service.DoctorService;
import com.vention.medfast.patient.service.PatientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceImplTest {
    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private ReferralService referralService;

    @Mock
    private DoctorService doctorService;

    @Mock
    private AppointmentMapper appointmentMapper;

    @Mock
    private PatientService patientService;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @Test
    void shouldCreateDraftWithReferral() {
        AppointmentCreateDto dto = new AppointmentCreateDto(
            10L,
            null,
            LocalDate.now().plusDays(1),
            LocalTime.of(10, 0)
        );
        Referral referral = new Referral();
        referral.setAppointmentType(AppointmentType.VISIT);
        Appointment appointment = new Appointment();
        when(referralService.getActiveById(10L)).thenReturn(referral);
        when(appointmentMapper.toAppointment(dto, referral.getIssuedBy(), referral.getPatient(), referral))
            .thenReturn(appointment);
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        Long id = appointmentService.createDraft(dto);
        assertThat(appointment.getStatus()).isEqualTo(Status.SCHEDULED);
        assertThat(appointment.getType()).isEqualTo(AppointmentType.VISIT);
    }

    @Test
    void shouldConfirmAppointmentAndMarkReferralUsed() {
        Referral referral = new Referral();
        referral.setStatus(ReferralStatus.ACTIVE);
        Appointment appointment = new Appointment();
        appointment.setStatus(Status.SCHEDULED);
        appointment.setReferral(referral);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        appointmentService.confirm(1L);
        assertThat(appointment.getStatus()).isEqualTo(Status.SCHEDULED_CONFIRMED);
        verify(referralService).markUsed(referral);
    }

    @Test
    void shouldThrowException_whenConfirmingNotDraftAppointment() {
        Appointment appointment = new Appointment();
        appointment.setStatus(Status.COMPLETED);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        assertThatThrownBy(() -> appointmentService.confirm(1L))
            .isInstanceOf(AppointmentException.class);
    }
}
