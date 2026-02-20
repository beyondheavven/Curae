package com.vention.medfast.auth.core;

import com.vention.medfast.doctor.service.DoctorService;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.user.model.RoleType;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserService userService;

	private final PatientService patientService;

	private final DoctorService doctorService;

	@Override
	public UserDetails loadUserByUsername(String username) {
		String normalizedUsername = username.toLowerCase();
		User user = userService.findByUsername(normalizedUsername);
		return loadUserEntity(user);
	}

	public UserDetails loadUserEntity(User user) {
		Long patientId = null;
		Long doctorId = null;

		if (user.getRole() == RoleType.PATIENT) {
			patientId = patientService.findIdByUserId(user.getId());
		}
		else if (user.getRole() == RoleType.DOCTOR) {
			doctorId = doctorService.findIdByUserId(user.getId());
		}

		return new CustomUserDetails(user, patientId, doctorId);
	}

}
