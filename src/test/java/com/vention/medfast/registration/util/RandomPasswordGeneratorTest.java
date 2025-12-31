package com.vention.medfast.registration.util;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RandomPasswordGeneratorTest {

	@Test
	void shouldThrowExceptionWhenLengthIsInvalid() {
		assertThatThrownBy(() -> RandomPasswordGenerator.generate(1)).isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Password length must be at least 2 characters");
	}

	@ParameterizedTest
	@ValueSource(ints = { 2, 8, 12, 40 })
	void shouldGeneratePasswordOfLengthWhenLengthIsCorrect(int length) {
		String password = RandomPasswordGenerator.generate(length);

		assertThat(password).isNotNull().hasSize(length);
	}

	@RepeatedTest(10)
	void shouldPasswordContainRequiredCharactersWhenInputIsValid() {
		String password = RandomPasswordGenerator.generate(8);

		assertThat(password).containsPattern("[a-z]").containsPattern("[0-9]");
	}

	@Test
	void shouldGeneratePasswordWithOnlyAllowedCharacters() {
		String password = RandomPasswordGenerator.generate(8);

		assertThat(password).matches("^[a-z0-9]+$");
	}

	@Test
	void shouldGenerateRandomPassword() {
		String password1 = RandomPasswordGenerator.generate(8);
		String password2 = RandomPasswordGenerator.generate(8);

		assertThat(password1).isNotEqualTo(password2);
	}

}
