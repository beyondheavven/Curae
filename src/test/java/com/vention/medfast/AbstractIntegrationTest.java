package com.vention.medfast;

import com.vention.medfast.config.PostgresTestContainersConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import({ PostgresTestContainersConfig.class })
public abstract class AbstractIntegrationTest {

}
