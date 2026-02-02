package com.vention.medfast.config;

import com.vention.medfast.refreshtoken.job.RefreshTokenCleanupJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import java.time.Duration;

@Configuration
public class QuartzConfig {

	@Bean
	public JobDetailFactoryBean jobDetailFactory() {
		JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
		jobDetailFactory.setJobClass(RefreshTokenCleanupJob.class);
		jobDetailFactory.setDescription("Refresh token cleanup job");
		jobDetailFactory.setDurability(true);
		return jobDetailFactory;
	}

	@Bean
	public SimpleTriggerFactoryBean triggerFactory(JobDetail job) {

		SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
		trigger.setJobDetail(job);
		trigger.setRepeatInterval(Duration.ofMinutes(30).toMillis());
		trigger.setJobDataMap(new JobDataMap());
		trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
		trigger.setName("refreshTokenCleanupTrigger");
		return trigger;
	}

}
