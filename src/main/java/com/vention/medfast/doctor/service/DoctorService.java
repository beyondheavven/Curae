package com.vention.medfast.doctor.service;

import com.vention.medfast.doctor.dto.DoctorProfileCreateDto;
import com.vention.medfast.doctor.dto.DoctorSearchResponseDto;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DoctorService {

	Doctor createDoctorProfile(User user, DoctorProfileCreateDto dto);

	Doctor getById(Long id);

	Page<DoctorSearchResponseDto> search(String query, Pageable pageable);

}
