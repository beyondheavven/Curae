package com.vention.medfast.patient.service;

import com.vention.medfast.address.model.Address;
import com.vention.medfast.patient.mapper.PatientMapper;
import com.vention.medfast.patient.model.Gender;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.model.Status;
import com.vention.medfast.patient.repository.PatientRepository;
import com.vention.medfast.registration.dto.PersonDto;
import com.vention.medfast.user.model.User;
import java.time.LocalDate;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

}
