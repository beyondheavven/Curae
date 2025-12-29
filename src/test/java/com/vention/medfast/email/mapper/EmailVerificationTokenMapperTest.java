package com.vention.medfast.email.mapper;

import com.vention.medfast.email.model.EmailTokenType;
import com.vention.medfast.email.model.EmailVerificationToken;
import com.vention.medfast.patient.model.PatientStatus;
import com.vention.medfast.user.model.User;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class EmailVerificationTokenMapperTest {

	private final EmailVerificationTokenMapper mapper = Mappers.getMapper(EmailVerificationTokenMapper.class);

	@Test
	void shouldBuildTokenWithDefaultValues() {
		User user = new User();
		user.setId(42L);

		String tokenHash = "abc123";
		Instant expiresAt = Instant.now().plusSeconds(3600);

		EmailVerificationToken result = mapper.buildToken(user, tokenHash, expiresAt);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isNull();
		assertThat(result.getUser()).isSameAs(user);
		assertThat(result.getTokenHash()).isEqualTo(tokenHash);
		assertThat(result.getExpiresAt()).isEqualTo(expiresAt);
		assertThat(result.getType()).isEqualTo(EmailTokenType.VERIFY_EMAIL);
		assertThat(result.getStatus()).isEqualTo(PatientStatus.UNVERIFIED);
		assertThat(result.getCode()).isNull();
		assertThat(result.getCreatedAt()).isNull();
	}

}
