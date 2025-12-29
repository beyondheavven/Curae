package com.vention.medfast.profile.mapper;

import com.vention.medfast.address.model.Address;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.profile.dto.request.ContactsUpdateDto;
import com.vention.medfast.profile.dto.request.PersonalDetailsUpdateDto;
import com.vention.medfast.profile.dto.request.ProfileAddressUpdateDto;
import com.vention.medfast.profile.dto.response.ContactsDto;
import com.vention.medfast.profile.dto.response.PersonalDetailsDto;
import com.vention.medfast.profile.dto.response.ProfileAddressDto;
import com.vention.medfast.profile.dto.response.ProfileResponseDto;
import com.vention.medfast.user.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

	@Mapping(target = "personalDetails", expression = "java(toPersonalDetails(user, patient))")
	@Mapping(target = "profileAddress", source = "patient.address")
	@Mapping(target = "contactsDto", expression = "java(toContacts(user, patient.getAddress()))")
	@Mapping(target = "status", source = "patient.patientStatus")
	ProfileResponseDto toDto(User user, Patient patient);

	@Mapping(target = "name", source = "user.firstName")
	@Mapping(target = "lastName", source = "user.lastName")
	@Mapping(target = "gender", source = "patient.gender")
	@Mapping(target = "citizenship", source = "patient.citizenship")
	@Mapping(target = "photoUrl", source = "user.profileUrl")
	PersonalDetailsDto toPersonalDetails(User user, Patient patient);

	ProfileAddressDto toProfileAddressDto(Address address);

	@Mapping(target = "phone", source = "address.phone")
	@Mapping(target = "email", source = "user.email")
	ContactsDto toContacts(User user, Address address);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "firstName", source = "name")
	@Mapping(target = "lastName", source = "lastName")
	@Mapping(target = "profileUrl", source = "photoUrl")
	void updateUser(PersonalDetailsUpdateDto dto, @MappingTarget User user);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "gender", source = "gender")
	@Mapping(target = "citizenship", source = "citizenship")
	void updatePatient(PersonalDetailsUpdateDto dto, @MappingTarget Patient patient);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateAddress(ProfileAddressUpdateDto dto, @MappingTarget Address address);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "phone", source = "phone")
	void updateAddressFromContacts(ContactsUpdateDto dto, @MappingTarget Address address);

}
