package com.vention.curae.user.service;

import com.vention.curae.registration.dto.DoctorRegistrationRequestDto;
import com.vention.curae.registration.dto.PersonDto;
import com.vention.curae.user.dto.UserResponseDto;
import com.vention.curae.user.dto.UserUpdateRequestDto;
import com.vention.curae.user.model.User;
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
