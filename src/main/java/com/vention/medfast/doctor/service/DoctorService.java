package com.vention.medfast.doctor.service;

import com.vention.medfast.doctor.dto.DoctorClinicAssignmentRequestDto;
import com.vention.medfast.doctor.dto.DoctorResponseDto;
import com.vention.medfast.doctor.dto.DoctorSearchDto;
import com.vention.medfast.doctor.dto.DoctorUpdateDto;
import com.vention.medfast.doctor.dto.DoctorSearchResponseDto;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.registration.dto.DoctorRegistrationRequestDto;
import com.vention.medfast.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DoctorService {

	Doctor createProfile(User user, DoctorRegistrationRequestDto dto);

	Page<DoctorSearchResponseDto> search(DoctorSearchDto searchDto, Pageable pageable);

	DoctorResponseDto setClinics(Long id, DoctorClinicAssignmentRequestDto dto);

	DoctorResponseDto getById(Long id);

	List<DoctorResponseDto> getAll();

	DoctorResponseDto update(Long id, DoctorUpdateDto dto);

	void delete(Long id);

	Doctor findDoctorById(Long id);

	Doctor getCurrentDoctor();

	Long findIdByUserId(Long userId);

}
