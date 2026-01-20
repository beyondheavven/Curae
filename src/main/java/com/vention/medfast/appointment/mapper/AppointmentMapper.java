package com.vention.medfast.appointment.mapper;

import com.vention.medfast.appointment.dto.AppointmentCreateDto;
import com.vention.medfast.appointment.dto.AppointmentCreateWithLabsDto;
import com.vention.medfast.appointment.dto.FirstAppointmentCreateDto;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.referral.model.Referral;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.patient.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", source = "dto.date")
    @Mapping(target = "time", source = "dto.time")
    @Mapping(target = "doctor", source = "doctor")
    @Mapping(target = "patient", source = "patient")
    @Mapping(target = "referral", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "type", source = "dto.type")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Appointment toFirstAppointment(FirstAppointmentCreateDto dto, Doctor doctor, Patient patient);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", source = "dto.date")
    @Mapping(target = "time", source = "dto.time")
    @Mapping(target = "doctor", source = "doctor")
    @Mapping(target = "patient", source = "patient")
    @Mapping(target = "referral", source = "referral")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Appointment toAppointment(AppointmentCreateDto dto, Doctor doctor, Patient patient, Referral referral);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", source = "dto.date")
    @Mapping(target = "time", source = "dto.time")
    @Mapping(target = "doctor", source = "doctor")
    @Mapping(target = "patient", source = "patient")
    @Mapping(target = "referral", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "labResults", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Appointment toLabAppointment(AppointmentCreateWithLabsDto dto, Doctor doctor, Patient patient);
}
