package com.vention.medfast;

import com.vention.medfast.config.PostgresTestContainersConfig;
import com.vention.medfast.email.mail.service.MailService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
@Import(PostgresTestContainersConfig.class)
@TestPropertySource(properties = { "STRIPE_SECRET_KEY=sk_test_dummy_value", "STRIPE_PUBLIC_KEY=pk_test_dummy_value",
		"WEBHOOK_SECRET_KEY=whsec_dummy_value", "PAYMENT_SUCCESS_URL=http://localhost:8080/success",
		"PAYMENT_CANCEL_URL=http://localhost:8080/cancel" })
public abstract class AbstractIntegrationTest {

	@MockitoBean
	protected MailService mailService;

}
