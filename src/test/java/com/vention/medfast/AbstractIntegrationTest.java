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
@TestPropertySource(properties = { "stripe.secret-key=sk_test_dummy_value", "stripe.public-key=pk_test_dummy_value",
		"stripe.webhook-secret=whsec_dummy_value", "stripe.payment-success-url=http://localhost:8080/success",
		"stripe.payment-cancel-url=http://localhost:8080/cancel" })
public abstract class AbstractIntegrationTest {

	@MockitoBean
	protected MailService mailService;

}
