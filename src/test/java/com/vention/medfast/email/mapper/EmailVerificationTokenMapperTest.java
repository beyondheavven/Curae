package com.vention.medfast.email.mapper;

import com.vention.medfast.email.model.EmailVerificationToken;
import com.vention.medfast.email.model.EmailTokenType;
import com.vention.medfast.email.model.EmailTokenStatus;
import com.vention.medfast.user.model.User;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class EmailVerificationTokenMapperTest {

	private final EmailVerificationTokenMapper mapper = Mappers.getMapper(EmailVerificationTokenMapper.class);

	@Test
	void shouldBuildTokenVerifyEmailWithDefaultValues() {
		User user = new User();
		user.setId(42L);

		String tokenHash = "abc123";
		Instant expiresAt = Instant.now().plusSeconds(3600);

		EmailVerificationToken result = mapper.buildToken(user, tokenHash, expiresAt, EmailTokenType.VERIFY_EMAIL);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isNull();
		assertThat(result.getUser()).isSameAs(user);
		assertThat(result.getTokenHash()).isEqualTo(tokenHash);
		assertThat(result.getExpiresAt()).isEqualTo(expiresAt);
		assertThat(result.getType()).isEqualTo(EmailTokenType.VERIFY_EMAIL);
		assertThat(result.getStatus()).isEqualTo(EmailTokenStatus.SENT);
		assertThat(result.getCode()).isNull();
		assertThat(result.getCreatedAt()).isNull();
	}

	@Test
	void shouldBuildTokenSetPasswordWithDefaultValues() {
		User user = new User();
		user.setId(42L);

		String tokenHash = "abc123";
		Instant expiresAt = Instant.now().plusSeconds(3600);

		EmailVerificationToken result = mapper.buildToken(user, tokenHash, expiresAt, EmailTokenType.SET_PASSWORD);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isNull();
		assertThat(result.getUser()).isSameAs(user);
		assertThat(result.getTokenHash()).isEqualTo(tokenHash);
		assertThat(result.getExpiresAt()).isEqualTo(expiresAt);
		assertThat(result.getType()).isEqualTo(EmailTokenType.SET_PASSWORD);
		assertThat(result.getStatus()).isEqualTo(EmailTokenStatus.SENT);
		assertThat(result.getCode()).isNull();
		assertThat(result.getCreatedAt()).isNull();
	}

	@Test
	void shouldReturnNullWhenInputIsNull() {
		EmailVerificationToken result = mapper.buildToken(null, null, null, null);

		assertThat(result).isNull();
	}

}
