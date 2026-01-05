package com.vention.medfast.globalsearch.controller;

import com.jayway.jsonpath.JsonPath;
import com.vention.medfast.AbstractIntegrationTest;
import com.vention.medfast.auth.dto.AuthRequestDto;
import com.vention.medfast.constants.ApiPaths;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SearchControllerTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private String token;

    @BeforeAll
    void obtainToken() throws Exception {
        token = loginAndGetToken("john.smith@mail.com", "pass");
    }

    @Test
    @DisplayName("Should return 200 and list of doctors/services when query is valid")
    void shouldReturnResultsWhenQueryIsValid() throws Exception {
        // Предполагаем, что в базе есть доктор "House" или специализация "Cardiologist"
        String query = "House";

        mockMvc.perform(get(ApiPaths.SEARCH_ENDPOINT)
                .header("Authorization", "Bearer " + token)
                .param("query", query)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.doctors").isArray())
            .andExpect(jsonPath("$.data.services").isArray())
            .andExpect(jsonPath("$.data.doctors[0].lastName", Matchers.containsStringIgnoringCase(query)));
    }

    @Test
    @DisplayName("Should return 404 EntityNotFoundException when no results match the query")
    void shouldReturnNotFoundWhenNoResultsMatch() throws Exception {
        String query = "NonExistentRandomString12345";

        mockMvc.perform(get(ApiPaths.SEARCH_ENDPOINT)
                .header("Authorization", "Bearer " + token)
                .param("query", query)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error.message").value("Your search returned no results"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when query is too short")
    void shouldReturnValidationErrorWhenQueryIsTooShort() throws Exception {
        String query = "A";

        mockMvc.perform(get(ApiPaths.SEARCH_ENDPOINT)
                .header("Authorization", "Bearer " + token)
                .param("query", query)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when token is invalid")
    void shouldReturnUnauthorizedWhenTokenIsInvalid() throws Exception {
        mockMvc.perform(get(ApiPaths.SEARCH_ENDPOINT)
                .header("Authorization", "Bearer invalid-token")
                .param("query", "Doctor"))
            .andExpect(status().isUnauthorized());
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        AuthRequestDto loginRequest = new AuthRequestDto(email, password);
        String json = objectMapper.writeValueAsString(loginRequest);

        String response = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        return JsonPath.read(response, "$.data.accessToken");
    }
}
