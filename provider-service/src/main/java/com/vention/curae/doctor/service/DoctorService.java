package com.vention.curae.doctor.service;

import com.vention.curae.doctor.dto.DoctorClinicAssignmentRequestDto;
import com.vention.curae.doctor.dto.DoctorResponseDto;
import com.vention.curae.doctor.dto.DoctorSearchDto;
import com.vention.curae.doctor.dto.DoctorSearchResponseDto;
import com.vention.curae.doctor.dto.DoctorUpdateDto;
import com.vention.curae.doctor.model.Doctor;
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