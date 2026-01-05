package com.vention.medfast.email.mapper;

import com.vention.medfast.email.model.EmailTokenType;
import com.vention.medfast.email.model.EmailVerificationToken;
import com.vention.medfast.user.model.User;
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
    EmailVerificationToken createResetPasswordToken(
        User user,
        Integer code,
        String tokenHash,
        Instant expiresAt
    );
}
