package com.vention.medfast.service;

import com.vention.medfast.model.User;

import java.util.Optional;

public interface UserService {
	User findById(Long id);
    Optional<User> findByEmail(String email);
}
