package com.vention.medfast.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

	@Value("${openapi.server-url}")
	private String serverUrl;

	@Value("${openapi.server-description}")
	private String serverDescription;

	@Bean
	public OpenAPI customOpenAPI() {
		String securitySchemeName = "bearer-jwt";

		return new OpenAPI().info(new Info().title("Medfast API Documentation").version("1.0").description("""
				Provides REST endpoints for managing patients, doctors, and appointments
				within the Medfast system. Enables appointment booking, modification,
				and cancellation, as well as administrative management of healthcare operations.
				""").contact(new Contact().name("Medfast Support Team").email("support@medfast.com")))
			.servers(List.of(new Server().url(serverUrl).description(serverDescription)))
			.tags(List.of(new Tag().name("Patients").description("Operations related to patient management"),
					new Tag().name("Doctors").description("Operations related to doctor management"),
					new Tag().name("Appointments").description("Operations related to appointment management")))
			.components(new Components().addSecuritySchemes(securitySchemeName,
					new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
			.addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
	}

}
