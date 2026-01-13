package com.vention.medfast.address.service;

import com.vention.medfast.address.mapper.AddressMapper;
import com.vention.medfast.address.model.Address;
import com.vention.medfast.address.repository.AddressRepository;
import com.vention.medfast.registration.dto.AddressDto;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

	@Mock
	private AddressRepository addressRepository;

	@Mock
	private AddressMapper addressMapper;

	@InjectMocks
	private AddressServiceImpl addressService;

	@Captor
	private ArgumentCaptor<Example<Address>> exampleCaptor;

	@Test
	void shouldCreateAndSaveAddressWhenNotExisting() {
		AddressDto dto = new AddressDto("  Main ", " City ", " 1A", "10 ", " State", "12345", "+123456789");
		Address mapped = new Address();
		mapped.setCity("City");

		Address saved = new Address();
		saved.setId(5L);
		saved.setCity("City");

		when(addressRepository.findOne(ArgumentMatchers.<Example<Address>>any())).thenReturn(Optional.empty());
		when(addressMapper.toEntity(dto)).thenReturn(mapped);
		when(addressRepository.save(mapped)).thenReturn(saved);

		Address result = addressService.create(dto);

		verify(addressRepository).findOne(exampleCaptor.capture());
		Address probe = exampleCaptor.getValue().getProbe();

		assertThat(result.getId()).isEqualTo(5L);
		assertThat(result.getCity()).isEqualTo("City");
		assertThat(probe.getStreet()).isEqualTo("Main");
		assertThat(probe.getCity()).isEqualTo("City");
		assertThat(probe.getApartment()).isEqualTo("1A");
		assertThat(probe.getHouseNumber()).isEqualTo("10");
		assertThat(probe.getState()).isEqualTo("State");
		assertThat(probe.getZipCode()).isEqualTo("12345");
		assertThat(probe.getPhone()).isNull();

		verify(addressMapper).toEntity(dto);
		verify(addressRepository).save(mapped);
	}

	@Test
	void shouldUpdatePhoneWhenAddressExistsAndPhoneMissing() {
		AddressDto dto = new AddressDto("Main", "City", "1A", "10", "State", "12345", "  +123456789  ");

		Address existing = new Address();
		existing.setId(2L);
		existing.setPhone("");

		when(addressRepository.findOne(ArgumentMatchers.<Example<Address>>any())).thenReturn(Optional.of(existing));
		when(addressRepository.save(existing)).thenReturn(existing);

		Address result = addressService.create(dto);

		assertThat(result.getId()).isEqualTo(2L);
		assertThat(result.getPhone()).isEqualTo("+123456789");

		verify(addressRepository, times(1)).findOne(exampleCaptor.capture());
		assertThat(exampleCaptor.getValue().getProbe().getPhone()).isNull();
		verify(addressRepository).save(existing);
		verify(addressMapper, never()).toEntity(any(AddressDto.class));
	}

	@Test
	void shouldReturnExistingWhenAddressAndPhonePresent() {
		AddressDto dto = new AddressDto("Main", "City", "1A", "10", "State", "12345", null);

		Address existing = new Address();
		existing.setId(3L);
		existing.setPhone("+111");

		when(addressRepository.findOne(ArgumentMatchers.<Example<Address>>any())).thenReturn(Optional.of(existing));

		Address result = addressService.create(dto);

		assertThat(result).isSameAs(existing);
		assertThat(result.getPhone()).isEqualTo("+111");

		verify(addressRepository).findOne(any(Example.class));
		verify(addressRepository, never()).save(any(Address.class));
		verify(addressMapper, never()).toEntity(any(AddressDto.class));
	}

}
