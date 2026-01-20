package com.vention.medfast.appointment.mapper;

import com.vention.medfast.appointment.dto.AppointmentCreateWithLabsDto;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.patient.model.Patient;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class AppointmentMapperTest {

    private AppointmentMapper appointmentMapper;

    @BeforeEach
    void setUp() {
        appointmentMapper = Mappers.getMapper(AppointmentMapper.class);
    }

    @Test
    void shouldMapCreateLabAppointmentRequestToEntity() {
        AppointmentCreateWithLabsDto dto = new AppointmentCreateWithLabsDto(5L, LocalDate.of(2030, 1, 1), LocalTime.of(9, 30), List.of(1L, 2L));
        Patient patient = new Patient();
        patient.setId(5L);
        Doctor doctor = new Doctor();
        doctor.setId(3L);
        Appointment appointment = appointmentMapper.toLabAppointment(dto, doctor, patient);
        assertThat(appointment).isNotNull();
        assertThat(appointment.getPatient()).isEqualTo(patient);
        assertThat(appointment.getDoctor()).isEqualTo(doctor);
        assertThat(appointment.getDate()).isEqualTo(dto.date());
        assertThat(appointment.getTime()).isEqualTo(dto.time());
        assertThat(appointment.getStatus()).isNull();
        assertThat(appointment.getType()).isNull();
    }
}
