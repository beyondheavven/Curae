package com.vention.medfast.user.service;

import com.vention.medfast.api.exception.EmailAlreadyExistsException;
import com.vention.medfast.patient.model.Gender;
import com.vention.medfast.registration.dto.PersonDto;
import com.vention.medfast.user.dto.UserResponseDto;
import com.vention.medfast.user.dto.UserUpdateRequestDto;
import com.vention.medfast.user.mapper.UserMapper;
import com.vention.medfast.user.model.RoleType;
import com.vention.medfast.user.model.Status;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserMapper userMapper;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserServiceImpl userService;

	@Test
	void shouldCreatePatientWhenEmailUnique() {
		PersonDto person = new PersonDto("John", "Doe", LocalDate.of(1990, 1, 1), Gender.MALE, "Password123",
				"john@mail.com", "USA");

		User mappedUser = new User();
		mappedUser.setEmail(person.getEmail());

		User savedUser = new User();
		savedUser.setId(1L);
		savedUser.setEmail(person.getEmail());

		when(userRepository.existsByEmail(person.getEmail())).thenReturn(false);
		when(userMapper.toPatientUser(person, passwordEncoder)).thenReturn(mappedUser);
		when(userRepository.save(mappedUser)).thenReturn(savedUser);

		User result = userService.createPatient(person);

		assertThat(result.getId()).isEqualTo(1L);
		assertThat(result.getEmail()).isEqualTo(person.getEmail());

		verify(userRepository).existsByEmail(person.getEmail());
		verify(userMapper).toPatientUser(person, passwordEncoder);
		verify(userRepository).save(mappedUser);
	}

	@Test
	void shouldThrowWhenEmailAlreadyExists() {
		PersonDto person = new PersonDto("John", "Doe", LocalDate.of(1990, 1, 1), Gender.MALE, "Password123",
				"john@mail.com", "USA");

		when(userRepository.existsByEmail(person.getEmail())).thenReturn(true);

		assertThatThrownBy(() -> userService.createPatient(person)).isInstanceOf(EmailAlreadyExistsException.class);

		verify(userRepository, times(1)).existsByEmail(person.getEmail());
		verifyNoMoreInteractions(userRepository);
		verifyNoMoreInteractions(userMapper);
	}

	@Test
	void shouldReturnUserResponseDtoWhenUserExists() {
		Long userId = 1L;
		User user = new User();
		user.setId(userId);
		user.setEmail("test@example.com");

		UserResponseDto expectedDto = new UserResponseDto(userId, "test@example.com", "John", "Doe", null, null,
				RoleType.PATIENT, Status.ACTIVE);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(userMapper.toResponseDto(user)).thenReturn(expectedDto);

		UserResponseDto result = userService.getById(userId);

		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(userId);
		assertThat(result.email()).isEqualTo("test@example.com");

		verify(userRepository).findById(userId);
		verify(userMapper).toResponseDto(user);
	}

	@Test
	void shouldThrowEntityNotFoundExceptionWhenUserDoesNotExist() {
		Long userId = 9999L;
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userService.getById(userId)).isInstanceOf(EntityNotFoundException.class)
			.hasMessage("User not found");

		verify(userRepository).findById(userId);
		verifyNoInteractions(userMapper);
	}

	@Test
	void shouldReturnPageOfUserResponseDtos() {
		Pageable pageable = PageRequest.of(0, 10);
		User user = new User();
		user.setId(1L);

		UserResponseDto dto = new UserResponseDto(1L, "mail@test.com", "Test", "User", null, null, RoleType.PATIENT,
				Status.ACTIVE);

		Page<User> userPage = new PageImpl<>(List.of(user));

		when(userRepository.findAll(pageable)).thenReturn(userPage);
		when(userMapper.toResponseDto(user)).thenReturn(dto);

		Page<UserResponseDto> result = userService.getAll(pageable);

		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().getFirst()).isEqualTo(dto);

		verify(userRepository).findAll(pageable);
		verify(userMapper).toResponseDto(user);
	}

	@Test
	void shouldReturnEmptyPageWhenNoUsersExist() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<User> emptyPage = new PageImpl<>(Collections.emptyList());

		when(userRepository.findAll(pageable)).thenReturn(emptyPage);

		Page<UserResponseDto> result = userService.getAll(pageable);

		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isZero();
		assertThat(result.getContent()).isEmpty();

		verify(userRepository).findAll(pageable);
		verifyNoInteractions(userMapper);
	}

	@Test
	void shouldUpdateUserWhenInputIsValid() {
		Long userId = 1L;
		User existingUser = new User();
		existingUser.setId(userId);
		existingUser.setEmail("old@mail.com");
		existingUser.setRole(RoleType.PATIENT);
		existingUser.setPasswordHash("oldPass");

		UserUpdateRequestDto dto = new UserUpdateRequestDto("new@mail.com", "newPass123", "newName", "Doe",
				LocalDate.of(1999, 1, 1), Status.ACTIVE, null);

		UserResponseDto expectedResponse = new UserResponseDto(userId, "new@mail.com", "newName", "Doe", null,
				LocalDate.of(1999, 1, 1), RoleType.PATIENT, Status.ACTIVE);

		when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
		when(userRepository.existsByEmail("new@mail.com")).thenReturn(false);
		when(userRepository.save(any(User.class))).thenReturn(existingUser);
		when(userMapper.toResponseDto(existingUser)).thenReturn(expectedResponse);

		UserResponseDto result = userService.updateUser(userId, dto);

		assertThat(result.email()).isEqualTo("new@mail.com");
		assertThat(result.firstName()).isEqualTo("newName");
		assertThat(result.lastName()).isEqualTo("Doe");

		verify(userRepository).save(existingUser);
	}

	@Test
	void shouldThrowEntityNotFoundExceptionWhenUpdateNonExistentUser() {
		Long userId = 9999L;
		UserUpdateRequestDto dto = new UserUpdateRequestDto("mail@test.com", "pass", "Name", "Last",
				LocalDate.of(2000, 1, 1), Status.ACTIVE, null);

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userService.updateUser(userId, dto)).isInstanceOf(EntityNotFoundException.class)
			.hasMessage("User not found");

		verify(userRepository, never()).existsByEmail(anyString());
		verify(userRepository, never()).save(any());
	}

	@Test
	void shouldThrowEmailAlreadyExistsExceptionWhenEmailIsTakenByAnotherUser() {
		Long userId = 1L;
		String existingEmail = "current@mail.com";
		String newEmail = "existing@mail.com";

		User user = new User();
		user.setId(userId);
		user.setEmail(existingEmail);

		UserUpdateRequestDto dto = new UserUpdateRequestDto(newEmail, "pass", "First", "Last", LocalDate.of(2000, 1, 1),
				Status.ACTIVE, null);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(userRepository.existsByEmail(newEmail)).thenReturn(true);

		assertThatThrownBy(() -> userService.updateUser(userId, dto)).isInstanceOf(EmailAlreadyExistsException.class)
			.hasMessage("Email already exists");

		verify(userRepository, never()).save(any());
	}

}
