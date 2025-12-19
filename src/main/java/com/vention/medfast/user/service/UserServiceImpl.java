package com.vention.medfast.user.service;

import com.vention.medfast.api.exception.EmailAlreadyExistsException;
import com.vention.medfast.registration.dto.PersonDto;
import com.vention.medfast.user.mapper.UserMapper;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final UserMapper userMapper;

	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional(readOnly = true)
	public User findById(Long id) {
		return userRepository.findById(id)
			.orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
	}

	@Override
	public boolean existsByEmail(String email) {
		if (userRepository.existsByEmail(email)) {
			return true;
		}
		return false;
	}

	@Override
	public User createPatient(PersonDto personDto) {
		if (userRepository.existsByEmail(personDto.getEmail())) {
			throw new EmailAlreadyExistsException("User with this email already exists");
		}

		User user = userMapper.toPatientUser(personDto, passwordEncoder);
		return userRepository.save(user);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

}
