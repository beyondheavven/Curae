package com.vention.medfast.address.service;

import com.vention.medfast.address.model.Address;
import com.vention.medfast.registration.dto.AddressDto;

public interface AddressService {

	Address create(AddressDto dto);

}
