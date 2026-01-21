package com.vention.medfast.user.service;

import com.vention.medfast.registration.dto.DoctorRegistrationRequestDto;
import com.vention.medfast.registration.dto.PersonDto;
import com.vention.medfast.user.dto.UserResponseDto;
import com.vention.medfast.user.dto.UserUpdateRequestDto;
import com.vention.medfast.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {

	User findById(Long id);

	User createPatient(PersonDto personDto);

	User createDoctor(DoctorRegistrationRequestDto doctorRegistrationDto, String tempPassword);

	Optional<User> findByEmail(String email);

	UserResponseDto getById(Long id);

	Page<UserResponseDto> getAll(Pageable pageable);

	UserResponseDto updateUser(Long id, UserUpdateRequestDto dto);

}
