package com.vention.medfast.address.service;

import com.vention.medfast.address.mapper.AddressMapper;
import com.vention.medfast.address.model.Address;
import com.vention.medfast.address.repository.AddressRepository;
import com.vention.medfast.registration.dto.AddressDto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

	private final AddressRepository addressRepository;

	private final AddressMapper addressMapper;

	@Override
	@Transactional
	public Address createAddress(AddressDto dto) {

		Address probe = new Address();
		probe.setStreet(trimToNull(dto.getStreet()));
		probe.setCity(trimToNull(dto.getCity()));
		probe.setApartment(trimToNull(dto.getApartment()));
		probe.setHouseNumber(trimToNull(dto.getHouseNumber()));
		probe.setState(trimToNull(dto.getState()));
		probe.setZipCode(trimToNull(dto.getZipCode()));

		ExampleMatcher matcher = ExampleMatcher.matchingAll()
			.withIgnoreNullValues()
			.withIgnorePaths("id", "phone")
			.withMatcher("streetName", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
			.withMatcher("cityName", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
			.withMatcher("stateName", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
			.withMatcher("zipCode", ExampleMatcher.GenericPropertyMatchers.exact())
			.withMatcher("houseNumber", ExampleMatcher.GenericPropertyMatchers.exact())
			.withMatcher("apartmentNumber", ExampleMatcher.GenericPropertyMatchers.exact());

		Optional<Address> existingOpt = addressRepository.findOne(Example.of(probe, matcher));

		if (existingOpt.isPresent()) {
			Address existing = existingOpt.get();

			String incomingPhone = trimToNull(dto.getPhoneNumber());
			if (incomingPhone != null && (existing.getPhone() == null || existing.getPhone().isBlank())) {
				existing.setPhone(incomingPhone);
				return addressRepository.save(existing);
			}

			return existing;
		}

		Address toCreate = addressMapper.toEntity(dto);
		return addressRepository.save(toCreate);
	}

	private String trimToNull(String s) {
		if (s == null)
			return null;
		String t = s.trim();
		return t.isEmpty() ? null : t;
	}

}
