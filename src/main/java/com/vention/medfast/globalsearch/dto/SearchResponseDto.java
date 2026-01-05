package com.vention.medfast.globalsearch.dto;

import com.vention.medfast.doctor.dto.DoctorBaseDto;
import com.vention.medfast.service.dto.ProvidedServiceBaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "Global search response containing full objects")
public record SearchResponseDto(

		@Schema(description = "List of doctors matching the criteria") List<DoctorBaseDto> doctors,

		@Schema(description = "List of services matching the criteria") List<ProvidedServiceBaseDto> services

) {
}
