package com.vention.medfast.config;

import com.vention.medfast.appointment.dto.OnlineConsultationDetails;
import com.vention.medfast.wrapper.CachedPage;
import com.vention.medfast.wrapper.CachedSlice;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.Map;

@EnableCaching
@Configuration
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
public class RedisCacheConfig {

	private static final Duration DEFAULT_TTL = Duration.ofSeconds(30);

	@Bean
	public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		JacksonJsonRedisSerializer<CachedSlice> sliceSerializer = new JacksonJsonRedisSerializer<>(CachedSlice.class);

		JacksonJsonRedisSerializer<CachedPage> pageSerializer = new JacksonJsonRedisSerializer<>(CachedPage.class);

		JacksonJsonRedisSerializer<OnlineConsultationDetails> detailsSerializer = new JacksonJsonRedisSerializer<>(
				OnlineConsultationDetails.class);

		RedisCacheConfiguration sliceConfig = RedisCacheConfiguration.defaultCacheConfig()
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(sliceSerializer))
			.entryTtl(DEFAULT_TTL);

		RedisCacheConfiguration pageConfig = RedisCacheConfiguration.defaultCacheConfig()
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(pageSerializer))
			.entryTtl(DEFAULT_TTL);

		RedisCacheConfiguration detailsConfig = RedisCacheConfiguration.defaultCacheConfig()
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(detailsSerializer))
			.entryTtl(DEFAULT_TTL);

		Map<String, RedisCacheConfiguration> configs = Map.of("patientVisits", sliceConfig, "patientTestHistory",
				pageConfig, "onlineConsultationDetails", detailsConfig);

		return RedisCacheManager.builder(connectionFactory)
			.withInitialCacheConfigurations(configs)
			.transactionAware()
			.build();
	}

}
