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

	@Mapping(target = "id", source = "id")
	@Mapping(target = "patientId", source = "patient.id")
	@Mapping(target = "doctorId", source = "doctor.id")
	@Mapping(target = "date", source = "date")
	@Mapping(target = "time", source = "time")
	@Mapping(target = "status", source = "status")
	@Mapping(target = "type", source = "type")
	@Mapping(target = "updatedAt", source = "updatedAt")
	@Mapping(target = "meetingLink", source = "meetingLink")
	OnlineConsultationDetails toDto(OnlineConsultation oc);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "referral", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    @Mapping(target = "type", constant = "ONLINE")
    @Mapping(target = "status", constant = "SCHEDULED")
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
