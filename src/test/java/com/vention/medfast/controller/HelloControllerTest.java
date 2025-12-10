package com.vention.medfast.controller;

import com.vention.medfast.constants.ApiPaths;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = HelloController.class,
		excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
				classes = com.vention.medfast.security.jwt.JwtAuthenticationFilter.class))
class HelloControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldReturnHelloWorldWhenGetHello() throws Exception {
		mockMvc.perform(get(ApiPaths.BASE + "/hello"))
			.andExpect(status().isOk())
			.andExpect(content().string("Hello World!"));
	}

}
