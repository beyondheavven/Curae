package com.vention.medfast.user.service;

import com.vention.medfast.user.model.User;
import java.util.Optional;

public interface UserService {

	User findById(Long id);
    Optional<User> findByEmail(String email);
}
