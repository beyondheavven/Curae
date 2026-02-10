package com.vention.medfast.config.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MaskingPatternLayoutTest {

	@Test
	void shouldMaskMessageInLayout() {
		LoggerContext context = new LoggerContext();

		MaskingPatternLayout layout = new MaskingPatternLayout();
		layout.setContext(context);
		layout.addMaskPattern("secret=(\\w+)");
		layout.setPattern("%msg");
		layout.start();

		ILoggingEvent event = mock(ILoggingEvent.class);
		when(event.getFormattedMessage()).thenReturn("secret=abcd");

		String result = layout.doLayout(event);

		assertThat(result).isEqualTo("secret=****");
	}

}
