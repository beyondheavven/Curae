package com.vention.medfast.clinic.service;

import com.vention.medfast.clinic.dto.ClinicRequestDto;
import com.vention.medfast.clinic.dto.ClinicResponseDto;
import com.vention.medfast.clinic.model.Clinic;
import java.util.List;
import java.util.Set;

public interface ClinicService {

	List<ClinicResponseDto> getAll();

	ClinicResponseDto getById(Long id);

	Long create(ClinicRequestDto dto);

	Long update(Long id, ClinicRequestDto dto);

	void delete(Long id);

	List<Clinic> findAllByIds(Set<Long> clinicIds);

	Clinic findById(Long id);

}
