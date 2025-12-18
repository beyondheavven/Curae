package com.vention.medfast.registration.service;

import com.vention.medfast.address.model.Address;
import com.vention.medfast.address.service.AddressService;
import com.vention.medfast.patient.model.Gender;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.registration.dto.AddressDto;
import com.vention.medfast.registration.dto.PersonDto;
import com.vention.medfast.registration.dto.RegistrationRequestDto;
import com.vention.medfast.registration.event.PatientRegisteredEvent;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.service.UserService;
import java.time.LocalDate;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

class RegistrationServiceImplTest {

	@Test
	void shouldInvokeAllDependenciesAndSendVerificationEmail() {
		FakeUserService userService = new FakeUserService();
		FakeAddressService addressService = new FakeAddressService();
		FakePatientService patientService = new FakePatientService();
		FakeEventPublisher eventPublisher = new FakeEventPublisher();

		RegistrationServiceImpl service = new RegistrationServiceImpl(userService, addressService, patientService,
				eventPublisher);

		RegistrationRequestDto request = new RegistrationRequestDto(
				new PersonDto("John", "Doe", LocalDate.of(1990, 1, 1), Gender.MALE, "Password123", "john@mail.com",
						"+123456789", "USA"),
				new AddressDto("Main", "City", "1A", "10", "State", "12345", "+123456789"));

		service.register(request);

		assertThat(userService.calledWith).isEqualTo(request.getPerson());
		assertThat(addressService.calledWith).isEqualTo(request.getAddress());
		assertThat(patientService.calledUser).isSameAs(userService.userToReturn);
		assertThat(patientService.calledAddress).isSameAs(addressService.addressToReturn);
		assertThat(patientService.calledPerson).isEqualTo(request.getPerson());
		assertThat(eventPublisher.callCount).isOne();
		assertThat(eventPublisher.publishedEvent).isInstanceOf(PatientRegisteredEvent.class);
		assertThat(((PatientRegisteredEvent) eventPublisher.publishedEvent).userId()).isEqualTo(1L);
	}

	@Test
	void shouldStopFlowWhenPatientCreationFails() {
		FakeUserService userService = new FakeUserService();
		FakeAddressService addressService = new FakeAddressService();
		FakePatientService patientService = new FakePatientService();
		patientService.throwOnCreate = true;
		FakeEventPublisher eventPublisher = new FakeEventPublisher();

		RegistrationServiceImpl service = new RegistrationServiceImpl(userService, addressService, patientService,
				eventPublisher);

		RegistrationRequestDto request = new RegistrationRequestDto(
				new PersonDto("John", "Doe", LocalDate.of(1990, 1, 1), Gender.MALE, "Password123", "john@mail.com",
						"+123456789", "USA"),
				new AddressDto("Main", "City", "1A", "10", "State", "12345", "+123456789"));

		assertThatThrownBy(() -> service.register(request)).isInstanceOf(IllegalArgumentException.class);

		assertThat(patientService.calledUser).isSameAs(userService.userToReturn);
		assertThat(eventPublisher.callCount).isZero();
	}

	private static class FakeUserService implements UserService {

		private final User userToReturn = new User();

		private PersonDto calledWith;

		@Override
		public Optional<User> findByEmail(String email) {
			return Optional.empty();
		}

		@Override
		public User findById(Long id) {
			return null;
		}

		@Override
		public boolean existsByEmail(String email) {
			return false;
		}

		@Override
		public User createPatient(PersonDto personDto) {
			this.calledWith = personDto;
			userToReturn.setEmail(personDto.getEmail());
			userToReturn.setId(1L);
			return userToReturn;
		}

	}

	private static class FakeAddressService implements AddressService {

		private final Address addressToReturn = new Address();

		private AddressDto calledWith;

		@Override
		public Address createAddress(AddressDto dto) {
			this.calledWith = dto;
			return addressToReturn;
		}

	}

	private static class FakePatientService implements PatientService {

		private User calledUser;

		private Address calledAddress;

		private PersonDto calledPerson;

		private boolean throwOnCreate;

		@Override
		public Patient createPatientProfile(User user, Address address, PersonDto personDto) {
			this.calledUser = user;
			this.calledAddress = address;
			this.calledPerson = personDto;
			if (throwOnCreate) {
				throw new IllegalArgumentException("Patient profile already exists");
			}
			return new Patient();
		}

		@Override
		public Patient getPatientByUserId(Long userId) {
			return null;
		}

	}

	private static class FakeEventPublisher implements ApplicationEventPublisher {

		private Object publishedEvent;

		private int callCount;

		@Override
		public void publishEvent(Object event) {
			this.publishedEvent = event;
			this.callCount++;
		}

	}

}
