package com.vention.medfast.appointment.mapper;

import com.vention.medfast.appointment.dto.CreateOnlineConsultationRequest;
import com.vention.medfast.appointment.dto.OnlineConsultationDetails;
import com.vention.medfast.appointment.dto.UpdateOnlineConsultationRequest;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.OnlineConsultation;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface OnlineConsultationMapper {

	@Mapping(target = "id", source = "appointmentId")
	@Mapping(target = "patientId", source = "appointment.patient.id")
	@Mapping(target = "doctorId", source = "appointment.doctor.id")
	@Mapping(target = "date", source = "appointment.date")
	@Mapping(target = "time", source = "appointment.time")
	@Mapping(target = "status", source = "appointment.status")
	@Mapping(target = "type", source = "appointment.type")
	@Mapping(target = "updatedAt", source = "appointment.updatedAt")
	@Mapping(target = "meetingLink", source = "meetingLink")
	OnlineConsultationDetails toDto(OnlineConsultation oc);

	@Mapping(target = "appointment", source = "dto")
	@Mapping(target = "appointmentId", ignore = true)
	OnlineConsultation toEntity(CreateOnlineConsultationRequest dto);

	@Mapping(target = "date", source = "date")
	@Mapping(target = "time", source = "time")
	@Mapping(target = "status", source = "status")
	@Mapping(target = "type", constant = "ONLINE")
	@Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
	@Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
	Appointment toAppointment(CreateOnlineConsultationRequest dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateOnlineConsultation(UpdateOnlineConsultationRequest req, @MappingTarget OnlineConsultation oc);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
	void updateAppointment(UpdateOnlineConsultationRequest req, @MappingTarget Appointment appt);

}
