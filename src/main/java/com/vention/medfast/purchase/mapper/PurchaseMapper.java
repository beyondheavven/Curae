package com.vention.medfast.purchase.mapper;

import com.vention.medfast.purchase.dto.PurchaseResponseDto;
import com.vention.medfast.purchase.model.Purchase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {

	@Mapping(target = "patientId", source = "patient.id")
	@Mapping(target = "serviceId", source = "providedService.id")
	PurchaseResponseDto toDto(Purchase purchase);

}
