package com.vention.medfast.patient.service;

import com.vention.medfast.address.dto.AddressCreateDto;
import com.vention.medfast.address.model.Address;
import com.vention.medfast.address.repository.AddressRepository;
import com.vention.medfast.patient.dto.PatientCreateDto;
import com.vention.medfast.patient.dto.PatientResponseDto;
import com.vention.medfast.patient.mapper.PatientMapper;
import com.vention.medfast.patient.model.Gender;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.model.Status;
import com.vention.medfast.patient.repository.PatientRepository;
import com.vention.medfast.registration.dto.PersonDto;
import com.vention.medfast.user.dto.UserCreateDto;
import com.vention.medfast.user.model.User;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.vention.medfast.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

	@Mock
	private PatientRepository patientRepository;

	@Mock
	private PatientMapper patientMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
	private PatientServiceImpl patientService;


	@Test
	void shouldCreatePatientProfileWhenNotExists() {
		User user = new User();
		user.setId(1L);

		Address address = new Address();
		address.setId(2L);

		PersonDto personDto = new PersonDto("John", "Doe", LocalDate.of(1990, 1, 1), Gender.MALE, "Password123",
				"john@mail.com", "+123456789", "USA");

		Patient mapped = new Patient();
		mapped.setStatus(Status.UNVERIFIED);

		when(patientRepository.existsByUser(user)).thenReturn(false);
		when(patientMapper.toEntity(personDto, user, address)).thenReturn(mapped);
		when(patientRepository.save(mapped)).thenReturn(mapped);

		Patient result = patientService.createPatientProfile(user, address, personDto);

		assertThat(result.getStatus()).isEqualTo(Status.UNVERIFIED);
		assertThat(mapped.getStatus()).isEqualTo(Status.UNVERIFIED);

		verify(patientRepository, times(1)).existsByUser(user);
		verify(patientMapper, times(1)).toEntity(personDto, user, address);
		verify(patientRepository, times(1)).save(mapped);
	}

	@Test
	void shouldThrowWhenPatientProfileAlreadyExists() {
		User user = new User();
		user.setId(1L);

		when(patientRepository.existsByUser(user)).thenReturn(true);

		PersonDto personDto = new PersonDto("John", "Doe", LocalDate.of(1990, 1, 1), Gender.MALE, "Password123",
				"john@mail.com", "+123456789", "USA");

		assertThrows(IllegalArgumentException.class,
				() -> patientService.createPatientProfile(user, new Address(), personDto));

		verify(patientRepository).existsByUser(user);
		verifyNoInteractions(patientMapper);
		verify(patientRepository, never()).save(any(Patient.class));
	}

    @Test
    void shouldCreatePatientWhenInputIsValid() {
        UserCreateDto userDto = new UserCreateDto("John", "Doe", "john@example.com", "password", "http://url");
        AddressCreateDto addressDto = new AddressCreateDto("Street", "City", "123", "State", "1", "00000", "+1234567890");
        PatientCreateDto createDto = new PatientCreateDto(userDto, addressDto, "USA", Gender.MALE, LocalDate.of(1990, 1, 1));

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

        when(userRepository.existsByEmail(userDto.email())).thenReturn(false);
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

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        UserCreateDto userDto = new UserCreateDto("John", "Doe", "john@example.com", "password", "http://url");
        PatientCreateDto createDto = new PatientCreateDto(userDto, null, "USA", Gender.MALE, LocalDate.now());

        when(userRepository.existsByEmail(userDto.email())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> patientService.create(createDto));

        verify(userRepository, never()).save(any());
        verify(patientRepository, never()).save(any());
    }

}
