package com.vention.medfast.doctor.mapper;

import com.vention.medfast.doctor.dto.DoctorProfileCreateDto;
import com.vention.medfast.doctor.dto.DoctorSearchResponseDto;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.model.DoctorDetails;
import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.user.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

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
	Doctor toDoctor(User user, DoctorProfileCreateDto dto, List<Specialization> specializations);

	DoctorDetails toDoctorDetails(DoctorProfileCreateDto dto);

	@Mapping(source = "user.firstName", target = "firstName")
	@Mapping(source = "user.lastName", target = "lastName")
	@Mapping(source = "user.profileUrl", target = "profileUrl")
	@Mapping(source = "specializations", target = "specialization", qualifiedByName = "specializationsToString")
	DoctorSearchResponseDto toDoctorSearchResponseDto(Doctor doctor);

	@AfterMapping
	default void linkSpecializationsAndGenerateBio(@MappingTarget Doctor doctor, List<Specialization> specializations) {
		specializations.forEach(doctor::addSpecialization);
		doctor.setBio("Specialist in " + specializations.getFirst().getName());
	}

	@Named("specializationsToString")
	default String specializationsToString(Set<Specialization> specializations) {
		if (isEmpty(specializations)) {
			return null;
		}
		return specializations.stream().map(Specialization::getName).collect(Collectors.joining(", "));
	}

}
