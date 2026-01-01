package com.vention.medfast.profile.service;

import com.vention.medfast.address.model.Address;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.profile.dto.request.ProfileRequestDto;
import com.vention.medfast.profile.dto.response.ProfileResponseDto;
import com.vention.medfast.profile.event.EmailChangedEvent;
import com.vention.medfast.profile.mapper.ProfileMapper;
import com.vention.medfast.user.model.Status;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ProfileServiceImpl implements ProfileService {

	private final UserService userService;

	private final PatientService patientService;

	private final ProfileMapper profileMapper;

	private final ApplicationEventPublisher eventPublisher;

	@Transactional(readOnly = true)
	@Override
	public ProfileResponseDto getProfile(Long userId) {
		User user = userService.findById(userId);
		Patient patient = patientService.getPatientByUserId(userId);

		return profileMapper.toDto(user, patient);
	}

	@Transactional
	@Override
	public void updateProfile(Long userId, ProfileRequestDto profileRequestDto) {

		User user = userService.findById(userId);
		Patient patient = patientService.getPatientByUserId(userId);
		Address address = patient.getAddress();

		String oldEmail = user.getEmail();

		if (profileRequestDto.getPersonalDetailsUpdateDto() != null) {
			profileMapper.updateUser(profileRequestDto.getPersonalDetailsUpdateDto(), user);
			profileMapper.updatePatient(profileRequestDto.getPersonalDetailsUpdateDto(), patient);
		}

		if (profileRequestDto.getProfileAddressUpdateDto() != null) {
			profileMapper.updateAddress(profileRequestDto.getProfileAddressUpdateDto(), address);
		}

		if (profileRequestDto.getContactsUpdateDto() != null) {
			profileMapper.updateAddressFromContacts(profileRequestDto.getContactsUpdateDto(), address);

			if (profileRequestDto.getContactsUpdateDto().getEmail() != null) {
				user.setEmail(profileRequestDto.getContactsUpdateDto().getEmail().trim());
			}
		}

		boolean emailChanged = user.getEmail() != null && !user.getEmail().equalsIgnoreCase(oldEmail);

		if (emailChanged) {
			user.setStatus(Status.UNVERIFIED);
			eventPublisher.publishEvent(new EmailChangedEvent(user.getId(), user.getEmail()));
		}
	}

}
