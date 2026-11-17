package com.vention.medfast.api.validation.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "validation.password")
public class PasswordValidationProperties {

	private String regex;

}
