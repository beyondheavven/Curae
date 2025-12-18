package com.vention.medfast;

import com.vention.medfast.config.PostgresTestContainersConfig;
import com.vention.medfast.email.mail.service.MailService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
@Import(PostgresTestContainersConfig.class)
public abstract class AbstractIntegrationTest {

	@MockitoBean
	protected MailService mailService;

}
