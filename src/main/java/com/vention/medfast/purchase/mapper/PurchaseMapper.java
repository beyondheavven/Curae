package com.vention.medfast.purchase.mapper;

import com.vention.medfast.purchase.dto.PurchaseResponseDto;
import com.vention.medfast.purchase.model.Purchase;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {

	PurchaseResponseDto toDto(Purchase purchase);

}
