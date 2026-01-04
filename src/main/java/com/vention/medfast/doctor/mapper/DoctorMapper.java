package com.vention.medfast.doctor.mapper;

import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.doctor.dto.DoctorBaseDto;
import com.vention.medfast.doctor.dto.DoctorProfileCreateDto;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.model.DoctorDetails;
import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.user.model.User;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
	@Mapping(source = "specializations", target = "specializations", qualifiedByName = "mapSpecializationToName")
	@Mapping(source = "clinics", target = "clinics", qualifiedByName = "mapClinicToName")
	DoctorBaseDto toBaseDto(Doctor doctor);

	List<DoctorBaseDto> toDtoList(List<Doctor> doctors);

	@AfterMapping
	default void linkSpecializationsAndGenerateBio(@MappingTarget Doctor doctor, List<Specialization> specializations) {
		specializations.forEach(doctor::addSpecialization);
		doctor.setBio("Specialist in " + specializations.getFirst().getName());
	}

	@Named("mapSpecializationToName")
	default List<String> mapSpecializationToName(Set<Specialization> specializations) {
		if (specializations == null)
			return List.of();
		return specializations.stream().map(Specialization::getName).collect(Collectors.toList());
	}

	@Named("mapClinicToName")
	default List<String> mapClinicToName(Set<Clinic> clinics) {
		if (clinics == null)
			return List.of();
		return clinics.stream().map(Clinic::getName).collect(Collectors.toList());
	}

}
