package com.vention.medfast.doctor.service;

import com.vention.medfast.doctor.dto.DoctorProfileCreateDto;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.user.model.User;

public interface DoctorService {

	Doctor createDoctorProfile(User user, DoctorProfileCreateDto dto);

}
