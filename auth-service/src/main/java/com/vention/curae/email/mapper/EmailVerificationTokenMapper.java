package com.vention.curae.email.mapper;

import com.vention.curae.email.model.EmailTokenType;
import com.vention.curae.email.model.EmailVerificationToken;
import com.vention.curae.user.model.User;
import java.time.Instant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmailVerificationTokenMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "type", source = "type")
	@Mapping(target = "code", ignore = true)
	@Mapping(target = "status", constant = "SENT")
	EmailVerificationToken buildToken(User user, String tokenHash, Instant expiresAt, EmailTokenType type);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "blockedUntil", ignore = true)
	@Mapping(target = "resendCount", constant = "0")
	@Mapping(target = "status", constant = "SENT")
	@Mapping(target = "type", constant = "RESET_PASSWORD")
	EmailVerificationToken createResetPasswordToken(User user, String code, String tokenHash, Instant expiresAt);

}
