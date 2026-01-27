package com.vention.medfast.referral.mapper;

import com.vention.medfast.referral.dto.ReferralCreateDto;
import com.vention.medfast.referral.dto.ReferralResponseDto;
import com.vention.medfast.referral.model.Referral;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.service.model.ProvidedService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReferralMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "patient", source = "patient")
	@Mapping(target = "service", source = "service")
	@Mapping(target = "issuedBy", source = "doctor")
	@Mapping(target = "expiresAt", source = "dto.expiresAt")
	@Mapping(target = "appointmentType", source = "dto.appointmentType")
	@Mapping(target = "status", constant = "ACTIVE")
	Referral toEntity(ReferralCreateDto dto, Patient patient, Doctor doctor, ProvidedService service);


	@Mapping(target = "serviceId", source = "service.id")
	ReferralResponseDto toDto(Referral referral);

}
