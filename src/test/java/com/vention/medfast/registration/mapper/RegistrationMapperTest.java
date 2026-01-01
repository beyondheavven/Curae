package com.vention.medfast.registration.mapper;

import com.vention.medfast.doctor.dto.DoctorProfileCreateDto;
import com.vention.medfast.registration.dto.DoctorRegistrationRequestDto;
import com.vention.medfast.registration.util.DataUtils;
import com.vention.medfast.user.dto.UserCreateDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrationMapperTest {

	private final RegistrationMapper mapper = Mappers.getMapper(RegistrationMapper.class);

	@Test
	void shouldMapRequestToUserCreateDtoWhenInputIsValid() {
		String tempPassword = "TempPassword123!";
		DoctorRegistrationRequestDto request = DataUtils.getDoctorRegistrationRequestDto();

		UserCreateDto userDto = mapper.toUserCreateDto(request, tempPassword);

		assertThat(userDto).isNotNull();
		assertThat(userDto.firstName()).isEqualTo(request.firstName());
		assertThat(userDto.lastName()).isEqualTo(request.lastName());
		assertThat(userDto.email()).isEqualTo(request.email());
		assertThat(userDto.birthDate()).isEqualTo(request.birthDate());
		assertThat(userDto.password()).isEqualTo(tempPassword);

		assertThat(userDto.profileUrl()).isNull();
	}

	@Test
	void shouldMapRequestToDoctorProfileDtoWhenInputIsValid() {
		DoctorRegistrationRequestDto request = DataUtils.getDoctorRegistrationRequestDto();

		DoctorProfileCreateDto profileDto = mapper.toDoctorProfileDto(request);

		assertThat(profileDto).isNotNull();
		assertThat(profileDto.licenseNumber()).isEqualTo(request.licenseNumber());
		assertThat(profileDto.phoneNumber()).isEqualTo(request.phoneNumber());
		assertThat(profileDto.specializationIds()).contains(1L, 2L);
	}

	@Test
	void shouldReturnNullToUserCreateDtoWhenInputIsNull() {
		assertThat(mapper.toUserCreateDto(null, null)).isNull();
	}

	@Test
	void shouldReturnNullToDoctorProfileDtoWhenInputIsNull() {
		assertThat(mapper.toDoctorProfileDto(null)).isNull();
	}

}
