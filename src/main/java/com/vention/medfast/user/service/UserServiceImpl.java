package com.vention.medfast.user.service;

import com.vention.medfast.api.exception.EmailAlreadyExistsException;
import com.vention.medfast.registration.dto.DoctorRegistrationRequestDto;
import com.vention.medfast.registration.dto.PersonDto;
import com.vention.medfast.user.dto.UserResponseDto;
import com.vention.medfast.user.dto.UserUpdateRequestDto;
import com.vention.medfast.user.mapper.UserMapper;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final UserMapper userMapper;

	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional(readOnly = true)
	public User findById(Long id) {
		return userRepository.findById(id)
			.orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
	}

	@Override
	public User createPatient(PersonDto personDto) {
		if (userRepository.existsByEmail(personDto.getEmail())) {
			throw new EmailAlreadyExistsException("User with this email already exists");
		}

		User user = userMapper.toPatientUser(personDto, passwordEncoder);
		return userRepository.save(user);
	}

	@Override
	public User createDoctor(DoctorRegistrationRequestDto doctorRegistrationDto, String tempPassword) {
		if (userRepository.existsByEmail(doctorRegistrationDto.email())) {
			throw new EmailAlreadyExistsException("User with this email already exists");
		}

		User user = userMapper.toDoctorUser(doctorRegistrationDto, tempPassword, passwordEncoder);
		return userRepository.save(user);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	@Transactional(readOnly = true)
	public User findByUsername(String username) {
		return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponseDto getById(Long id) {
		return userRepository.findById(id)
			.map(userMapper::toResponseDto)
			.orElseThrow(() -> new EntityNotFoundException("User not found"));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<UserResponseDto> getAll(Pageable pageable) {
		Page<User> users = userRepository.findAll(pageable);
		return users.map(userMapper::toResponseDto);
	}

	@Override
	@Transactional
	public UserResponseDto updateUser(Long id, UserUpdateRequestDto dto) {
		User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));

		if (!user.getEmail().equalsIgnoreCase(dto.email()) && userRepository.existsByEmail(dto.email())) {
			throw new EmailAlreadyExistsException("Email already exists");
		}

		userMapper.updateUserFromDto(user, dto, passwordEncoder);

		return userMapper.toResponseDto(userRepository.save(user));
	}

}
