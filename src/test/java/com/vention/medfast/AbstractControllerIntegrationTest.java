package com.vention.medfast;

import com.vention.medfast.appointment.service.cache.VisitCacheService;
import com.vention.medfast.auth.dto.AuthRequestDto;
import com.vention.medfast.auth.service.AuthService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractControllerIntegrationTest extends AbstractIntegrationTest {

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	protected EntityManager em;

	@MockitoBean
	protected VisitCacheService visitCacheService;

	@Autowired
	protected AuthService authService;

	private String token;

	@BeforeEach
	void setup() {
		login("admin@med.com", "pass");
	}

	protected ResultActions performRequest(HttpMethod method, String url, Object body, Object... uriVars)
			throws Exception {
		MockHttpServletRequestBuilder requestBuilder;

		if (method == GET) {
			requestBuilder = get(url, uriVars);
		}
		else if (method == POST) {
			requestBuilder = post(url, uriVars);
		}
		else if (method == PUT) {
			requestBuilder = put(url, uriVars);
		}
		else if (method == DELETE) {
			requestBuilder = delete(url, uriVars);
		}
		else {
			throw new IllegalArgumentException("Unsupported HTTP method: " + method);
		}
		requestBuilder.header("Authorization", "Bearer " + token);

		if (body != null) {
			requestBuilder.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(body));
		}

		return mockMvc.perform(requestBuilder);
	}

	protected void login(String email, String pass) {
		AuthRequestDto loginRequest = new AuthRequestDto(email, pass);

		token = authService.login(loginRequest).accessToken();
	}

}
