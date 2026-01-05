package com.vention.medfast.refreshtoken.repository;

import com.vention.medfast.refreshtoken.model.RefreshToken;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

	Optional<RefreshToken> findByToken(String token);

}
