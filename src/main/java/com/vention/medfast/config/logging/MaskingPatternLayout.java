package com.vention.medfast.config.logging;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class MaskingPatternLayout extends PatternLayout {

	private final RegexMaskerHelper regexMaskerHelper = new RegexMaskerHelper();

	public void addMaskPattern(String maskPattern) {
		regexMaskerHelper.addMaskPattern(maskPattern);
	}

	@Override
	public String doLayout(ILoggingEvent event) {
		String message = super.doLayout(event);
		return regexMaskerHelper.maskMessage(message);
	}

}
