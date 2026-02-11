package com.vention.medfast.config.logging;

import com.fasterxml.jackson.core.JsonStreamContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class CustomDataMaskerTest {

	@Test
	void shouldMaskCharSequenceValue() {
		CustomDataMasker masker = new CustomDataMasker();
		masker.setMaskPattern("token=(\\w+)");

		Object result = masker.mask(mock(JsonStreamContext.class), "token=abcd");

		assertThat(result).isEqualTo("token=****");
	}

	@Test
	void shouldReturnSameObjectWhenNotCharSequence() {
		CustomDataMasker masker = new CustomDataMasker();

		Object value = 123;
		Object result = masker.mask(mock(JsonStreamContext.class), value);

		assertThat(result).isSameAs(value);
	}

}
