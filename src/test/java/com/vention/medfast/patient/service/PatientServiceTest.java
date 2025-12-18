package com.vention.medfast.patient.service;

import com.vention.medfast.address.dto.AddressCreateDto;
import com.vention.medfast.patient.dto.PatientCreateDto;
import com.vention.medfast.patient.dto.PatientResponseDto;
import com.vention.medfast.patient.mapper.PatientMapper;
import com.vention.medfast.patient.model.GenderType;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.repository.PatientRepository;
import com.vention.medfast.user.dto.UserCreateDto;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

	@Mock
	private PatientRepository patientRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PatientMapper patientMapper;

	@InjectMocks
	private PatientServiceImpl patientService;

	@Test
	void shouldCreatePatientWhenInputIsValid() {
		UserCreateDto userDto = new UserCreateDto("John", "Doe", "john@example.com", "password", "http://url");
		AddressCreateDto addressDto = new AddressCreateDto("Street", "City", "123", "State", "1", "00000",
				"+1234567890");
		PatientCreateDto createDto = new PatientCreateDto(userDto, addressDto, "USA", GenderType.MALE,
				LocalDate.of(1990, 1, 1));

		User savedUser = new User();
		savedUser.setId(100L);
		savedUser.setEmail(userDto.email());

		Patient mappedPatient = new Patient();
		mappedPatient.setCitizenship("USA");

		Patient savedPatient = new Patient();
		savedPatient.setId(1L);
		savedPatient.setUser(savedUser);
		savedPatient.setCitizenship("USA");

		PatientResponseDto responseDto = PatientResponseDto.builder().id(1L).firstName("John").lastName("Doe").build();

		when(userRepository.save(any(User.class))).thenReturn(savedUser);
		when(patientMapper.toEntity(createDto)).thenReturn(mappedPatient);
		when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);
		when(patientMapper.toDto(savedPatient)).thenReturn(responseDto);

		PatientResponseDto result = patientService.create(createDto);

		assertNotNull(result);
		assertEquals(1L, result.id());
		assertEquals("John", result.firstName());

		verify(userRepository).save(any(User.class));
		verify(patientMapper).toEntity(createDto);
		verify(patientRepository).save(any(Patient.class));
		verify(patientMapper).toDto(savedPatient);
	}

	@Test
	void shouldReturnPatientWhenIdExists() {
		Long patientId = 1L;
		Patient patient = new Patient();
		patient.setId(patientId);

		PatientResponseDto responseDto = PatientResponseDto.builder().id(patientId).build();

		when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
		when(patientMapper.toDto(patient)).thenReturn(responseDto);

		PatientResponseDto result = patientService.getPatientById(patientId);

		assertNotNull(result);
		assertEquals(patientId, result.id());

		verify(patientRepository).findById(patientId);
		verify(patientMapper).toDto(patient);
	}

	@Test
	void shouldThrowExceptionWhenPatientNotFoundById() {
		Long patientId = 999L;
		when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
				() -> patientService.getPatientById(patientId));

		assertEquals("Patient not found", exception.getMessage());

		verify(patientRepository).findById(patientId);
		verifyNoInteractions(patientMapper);
	}

}
