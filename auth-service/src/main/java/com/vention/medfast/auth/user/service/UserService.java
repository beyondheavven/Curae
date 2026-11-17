package com.vention.medfast.auth.user.service;

import com.vention.medfast.auth.registration.dto.DoctorRegistrationRequestDto;
import com.vention.medfast.auth.registration.dto.PersonDto;
import com.vention.medfast.auth.user.dto.UserResponseDto;
import com.vention.medfast.auth.user.dto.UserUpdateRequestDto;
import com.vention.medfast.auth.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {

	User findById(Long id);

	User createPatient(PersonDto personDto);

	User createDoctor(DoctorRegistrationRequestDto doctorRegistrationDto, String tempPassword);

	Optional<User> findByEmail(String email);

	User findByUsername(String username);

	UserResponseDto getById(Long id);

	Page<UserResponseDto> getAll(Pageable pageable);

	UserResponseDto updateUser(Long id, UserUpdateRequestDto dto);

}
