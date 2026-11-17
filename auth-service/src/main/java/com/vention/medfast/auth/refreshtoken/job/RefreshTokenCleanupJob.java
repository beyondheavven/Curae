package com.vention.medfast.auth.refreshtoken.job;

import com.vention.medfast.auth.refreshtoken.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupJob implements Job {

	private final RefreshTokenService refreshTokenService;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) {
		refreshTokenService.deleteAllExpired();
	}

}
