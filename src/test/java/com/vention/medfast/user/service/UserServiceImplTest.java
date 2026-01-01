package com.vention.medfast.user.service;

import com.vention.medfast.api.exception.EmailAlreadyExistsException;
import com.vention.medfast.patient.model.Gender;
import com.vention.medfast.registration.dto.PersonDto;
import com.vention.medfast.user.mapper.UserMapper;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.repository.UserRepository;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
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

		assertThrows(EmailAlreadyExistsException.class, () -> userService.createPatient(person));

		verify(userRepository, times(1)).existsByEmail(person.getEmail());
		verifyNoMoreInteractions(userRepository);
		verifyNoMoreInteractions(userMapper);
	}

}
