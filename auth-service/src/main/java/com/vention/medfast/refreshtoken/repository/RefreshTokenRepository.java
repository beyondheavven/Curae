package com.vention.medfast.refreshtoken.repository;

import com.vention.medfast.refreshtoken.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

	Optional<RefreshToken> findByToken(String token);

	@Query("Select r FROM RefreshToken r WHERE r.expirationTime < :now")
	List<RefreshToken> findAllExpired(@Param("now") Instant now);

}
