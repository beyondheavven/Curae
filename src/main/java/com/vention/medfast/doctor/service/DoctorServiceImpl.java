package com.vention.medfast.doctor.service;

import com.vention.medfast.doctor.dto.DoctorProfileCreateDto;
import com.vention.medfast.doctor.mapper.DoctorMapper;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.repository.DoctorRepository;
import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.specialization.service.SpecializationService;
import com.vention.medfast.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

	private final DoctorMapper doctorMapper;

	private final DoctorRepository doctorRepository;

	private final SpecializationService specializationService;

	@Override
	@Transactional
	public Doctor createDoctorProfile(User user, DoctorProfileCreateDto dto) {
		List<Specialization> specializations = specializationService.findAllByIds(dto.specializationIds());

		Doctor doctor = doctorMapper.toDoctor(user, dto, specializations);

		return doctorRepository.save(doctor);
	}

}
