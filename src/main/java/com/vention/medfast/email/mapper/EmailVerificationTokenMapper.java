package com.vention.medfast.email.mapper;

import com.vention.medfast.email.model.EmailVerificationToken;
import com.vention.medfast.user.model.User;
import java.time.Instant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmailVerificationTokenMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "type", constant = "VERIFY_EMAIL")
	@Mapping(target = "code", ignore = true)
	@Mapping(target = "status", constant = "UNVERIFIED")
	EmailVerificationToken buildToken(User user, String tokenHash, Instant expiresAt);

}
