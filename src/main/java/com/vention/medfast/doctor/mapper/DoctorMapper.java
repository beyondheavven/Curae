package com.vention.medfast.doctor.mapper;

import com.vention.medfast.doctor.dto.DoctorProfileCreateDto;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.model.DoctorDetails;
import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.user.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "specializations", ignore = true)
	@Mapping(target = "user", source = "user")
	@Mapping(target = "details", source = "dto")
	@Mapping(target = "bio", ignore = true)
	Doctor toDoctor(User user, DoctorProfileCreateDto dto, List<Specialization> specializations);

	DoctorDetails toDoctorDetails(DoctorProfileCreateDto dto);

	@AfterMapping
	default void linkSpecializationsAndGenerateBio(@MappingTarget Doctor doctor, List<Specialization> specializations) {
		specializations.forEach(doctor::addSpecialization);

		doctor.setBio("Specialist in " + specializations.getFirst().getName());
	}

}
