package com.vention.medfast.doctor.mapper;

import com.vention.medfast.address.dto.ClinicAddressBaseDto;
import com.vention.medfast.address.model.Address;
import com.vention.medfast.clinic.dto.ClinicBaseDto;
import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.doctor.dto.DoctorResponseDto;
import com.vention.medfast.doctor.dto.DoctorSearchResponseDto;
import com.vention.medfast.doctor.dto.DoctorUpdateDto;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.model.DoctorDetails;
import com.vention.medfast.registration.dto.DoctorRegistrationRequestDto;
import com.vention.medfast.specialization.dto.SpecializationBaseDto;
import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.user.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "specializations", ignore = true)
	@Mapping(target = "user", source = "user")
	@Mapping(target = "details", source = "dto")
	@Mapping(target = "bio", ignore = true)
	Doctor toDoctor(User user, DoctorRegistrationRequestDto dto, List<Specialization> specializations);

	DoctorDetails toDoctorDetails(DoctorRegistrationRequestDto dto);

	@Mapping(target = "firstName", source = "user.firstName")
	@Mapping(target = "lastName", source = "user.lastName")
	@Mapping(target = "email", source = "user.email")
	@Mapping(target = "licenseNumber", source = "details.licenseNumber")
	@Mapping(target = "phoneNumber", source = "details.phoneNumber")
	@Mapping(target = "specializations", source = "specializations")
	@Mapping(target = "clinics", source = "clinics")
	DoctorResponseDto toDoctorResponseDto(Doctor doctor);

	@Mapping(source = "user.firstName", target = "firstName")
	@Mapping(source = "user.lastName", target = "lastName")
	@Mapping(source = "user.profileUrl", target = "profileUrl")
	@Mapping(source = "specializations", target = "specialization", qualifiedByName = "specializationsToString")
	DoctorSearchResponseDto toDoctorSearchResponseDto(Doctor doctor);

	SpecializationBaseDto toSpecializationBaseDto(Specialization specialization);

	ClinicBaseDto toClinicBaseDto(Clinic clinic);

	ClinicAddressBaseDto toAddressDto(Address address);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateDoctorFromDto(DoctorUpdateDto dto, @MappingTarget Doctor doctor);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateUserFromDto(DoctorUpdateDto dto, @MappingTarget User user);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateDetailsFromDto(DoctorUpdateDto dto, @MappingTarget DoctorDetails doctorDetails);

	@AfterMapping
	default void linkSpecializationsAndGenerateBio(@MappingTarget Doctor doctor, List<Specialization> specializations) {
		if (!isEmpty(specializations)) {
			specializations.forEach(doctor::addSpecialization);
			doctor.setBio("Specialist in " + specializations.getFirst().getName());
		}
	}

	@Named("specializationsToString")
	default String specializationsToString(Set<Specialization> specializations) {
		if (isEmpty(specializations)) {
			return null;
		}
		return specializations.stream().map(Specialization::getName).collect(Collectors.joining(", "));
	}

}
