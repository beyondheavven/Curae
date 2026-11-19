package com.vention.curae.gateway;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gateway")
public class GatewayProperties {

	private final List<String> publicPaths = new ArrayList<>();

	private final List<Service> services = new ArrayList<>();

	private String jwtSecret = "";

	public List<String> getPublicPaths() {
		return publicPaths;
	}

	public List<Service> getServices() {
		return services;
	}

	public String getJwtSecret() {
		return jwtSecret;
	}

	public void setJwtSecret(String jwtSecret) {
		this.jwtSecret = jwtSecret;
	}

	public static class Service {

		private String name = "";

		private String url = "";

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

	}

}
