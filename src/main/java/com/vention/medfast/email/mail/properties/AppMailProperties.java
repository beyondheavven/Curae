package com.vention.medfast.email.mail.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.mail")
public class AppMailProperties {

	private String from;

	private Templates templates = new Templates();

	private Subjects subjects = new Subjects();

	private Paths paths = new Paths();

	@Getter
	@Setter
	public static class Templates {

		private String verifyEmail;

		private String resetPassword;

	}

	@Getter
	@Setter
	public static class Subjects {

		private String verifyEmail;

		private String resetPassword;

	}

	@Getter
	@Setter
	public static class Paths {

		private String verifyEmail;

		private String resetPassword;

	}

}
