package com.vention.medfast.doctor.service;

import com.vention.medfast.doctor.dto.DoctorClinicAssignmentRequestDto;
import com.vention.medfast.doctor.dto.DoctorResponseDto;
import com.vention.medfast.doctor.dto.DoctorSearchDto;
import com.vention.medfast.doctor.dto.DoctorSearchResponseDto;
import com.vention.medfast.doctor.dto.DoctorUpdateDto;
import com.vention.medfast.doctor.model.Doctor;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DoctorService {

	Page<DoctorSearchResponseDto> search(DoctorSearchDto searchDto, Pageable pageable);

	DoctorResponseDto setClinics(Long id, DoctorClinicAssignmentRequestDto dto);

	DoctorResponseDto getById(Long id);

	List<DoctorResponseDto> getAll();

	DoctorResponseDto update(Long id, DoctorUpdateDto dto);

	void delete(Long id);

	Doctor findDoctorById(Long id);

	Long findIdByUserId(Long userId);

}