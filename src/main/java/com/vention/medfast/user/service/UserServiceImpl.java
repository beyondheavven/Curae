package com.vention.medfast.user.service;

import com.vention.medfast.user.model.User;
import com.vention.medfast.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	@Transactional(readOnly = true)
	public User findById(Long id) {
		return userRepository.findById(id)
			.orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
	}

}
