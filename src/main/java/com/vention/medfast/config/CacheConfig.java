package com.vention.medfast.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig {

	@Bean
	public CaffeineCacheManager cacheManager() {
		CaffeineCacheManager manager = new CaffeineCacheManager("patientVisits", "onlineConsultationDetails",
				"patientTestHistory");

		manager.setCaffeine(Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).maximumSize(10_000));

		return manager;
	}

}
