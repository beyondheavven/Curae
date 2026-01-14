package com.vention.medfast.user.service;

import com.vention.medfast.registration.dto.DoctorRegistrationRequestDto;
import com.vention.medfast.registration.dto.PersonDto;
import com.vention.medfast.user.model.User;

import java.util.Optional;

public interface UserService {

	User findById(Long id);

	boolean existsByEmail(String email);

	User createPatient(PersonDto personDto);

	User createDoctor(DoctorRegistrationRequestDto doctorRegistrationDto, String tempPassword);

	Optional<User> findByEmail(String email);

}
