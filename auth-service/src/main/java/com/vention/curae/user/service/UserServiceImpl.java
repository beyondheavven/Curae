package com.vention.curae.user.service;

import com.vention.curae.api.exception.EmailAlreadyExistsException;
import com.vention.curae.api.exception.UserNotActiveException;
import com.vention.curae.email.service.EmailVerificationTokenService;
import com.vention.curae.event.AuthEventPublisher;
import com.vention.curae.event.UserIdentityUpdatedEvent;
import com.vention.curae.registration.dto.DoctorRegistrationRequestDto;
import com.vention.curae.registration.dto.PersonDto;
import com.vention.curae.user.dto.UserResponseDto;
import com.vention.curae.user.dto.UserUpdateRequestDto;
import com.vention.curae.user.mapper.UserMapper;
import com.vention.curae.user.model.RoleType;
import com.vention.curae.user.model.Status;
import com.vention.curae.user.model.User;
import com.vention.curae.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final UserMapper userMapper;

	private final PasswordEncoder passwordEncoder;

	private final EmailVerificationTokenService emailVerificationTokenService;

	private final AuthEventPublisher authEventPublisher;

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
		Optional<User> byEmail = userRepository.findByEmail(email.toLowerCase());
		if (byEmail.isPresent() && byEmail.get().getStatus() == Status.UNVERIFIED) {
			throw new UserNotActiveException("User is not verified");
		}
		return byEmail;
	}

	@Override
	@Transactional(readOnly = true)
	public User findByUsername(String username) {
		return userRepository.findByEmail(username.toLowerCase())
			.orElseThrow(() -> new UsernameNotFoundException("User not found"));
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

		boolean emailChanged = !user.getEmail().equalsIgnoreCase(dto.email());
		if (emailChanged && userRepository.existsByEmail(dto.email())) {
			throw new EmailAlreadyExistsException("Email already exists");
		}

		String oldEmail = user.getEmail();
		String oldFirstName = user.getFirstName();
		String oldLastName = user.getLastName();
		LocalDate oldBirthDate = user.getBirthDate();
		Status oldStatus = user.getStatus();
		String oldProfileUrl = user.getProfileUrl();

		userMapper.updateUserFromDto(user, dto, passwordEncoder);

		if (emailChanged) {
			user.setStatus(Status.UNVERIFIED);
		}

		User saved = userRepository.save(user);

		if (emailChanged) {
			if (saved.getRole() == RoleType.PATIENT) {
				String rawToken = emailVerificationTokenService.createForPatient(saved);
				authEventPublisher.userRegistered(saved.getId(), saved.getEmail(), saved.getFirstName(),
						saved.getLastName(), rawToken);
			}
			else if (saved.getRole() == RoleType.DOCTOR) {
				String rawToken = emailVerificationTokenService.createDoctorWelcomeToken(saved);
				authEventPublisher.doctorWelcomeRequested(saved.getEmail(), null, rawToken);
			}
		}

		boolean identityChanged = emailChanged || !Objects.equals(oldFirstName, saved.getFirstName())
				|| !Objects.equals(oldLastName, saved.getLastName())
				|| !Objects.equals(oldBirthDate, saved.getBirthDate()) || !Objects.equals(oldStatus, saved.getStatus())
				|| !Objects.equals(oldProfileUrl, saved.getProfileUrl());

		if (identityChanged) {
			authEventPublisher
				.userIdentityUpdated(new UserIdentityUpdatedEvent(saved.getId(), saved.getEmail(), saved.getFirstName(),
						saved.getLastName(), saved.getBirthDate() != null ? saved.getBirthDate().toString() : null,
						saved.getStatus().name(), Instant.now()));
		}

		return userMapper.toResponseDto(saved);
	}

}
