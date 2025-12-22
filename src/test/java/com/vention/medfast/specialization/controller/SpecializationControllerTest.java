package com.vention.medfast.specialization.controller;

import com.jayway.jsonpath.JsonPath;
import com.vention.medfast.AbstractIntegrationTest;
import com.vention.medfast.auth.dto.AuthRequestDto;
import com.vention.medfast.specialization.dto.SpecializationCreateDto;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpecializationControllerTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private String token;

    @BeforeAll
    void obtainToken() throws Exception {
        AuthRequestDto loginRequest = new AuthRequestDto("admin@med.com", "pass");
        String json = objectMapper.writeValueAsString(loginRequest);

        String response = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        token = JsonPath.read(response, "$.data.accessToken");
    }

    @Test
    void shouldReturnSpecializationByIdWhenInputIsValid() throws Exception {
        mockMvc.perform(get("/specializations/{id}", 1).header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.name").value("General Practitioner"))
            .andExpect(
                jsonPath("$.data.description").value("Primary care provider dealing with a wide range of diseases"))
            .andExpect(jsonPath("$.data.services").isArray())
            .andExpect(jsonPath("$.data.services.length()").value(2))
            .andExpect(jsonPath("$.data.services[0].id").value(1))
            .andExpect(jsonPath("$.data.services[0].name").value("General Checkup"))
            .andExpect(jsonPath("$.data.services[0].description").value("Routine physical examination"))
            .andExpect(jsonPath("$.data.services[0].price").value(150.0))
            .andExpect(jsonPath("$.data.services[0].duration").value(30))
            .andExpect(jsonPath("$.data.services[1].id").value(3))
            .andExpect(jsonPath("$.data.services[1].name").value("Quick Consultation"))
            .andExpect(jsonPath("$.data.services[1].description").value("Brief medical advice"))
            .andExpect(jsonPath("$.data.services[1].price").value(75.0))
            .andExpect(jsonPath("$.data.services[1].duration").value(15));
    }

    @Test
    void shouldReturn404WhenSpecializationNotFound() throws Exception {
        long notExistingId = 999L;

        mockMvc.perform(get("/specializations/{id}", notExistingId).header("Authorization", "Bearer " + token))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error.message").value("Specialization not found"));
    }

    @Test
    void shouldReturnSpecializationListWhenSpecializationsPresent() throws Exception {
        mockMvc.perform(get("/specializations").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data", Matchers.hasSize(Matchers.greaterThanOrEqualTo(3))))
            .andExpect(jsonPath("$.data[*].id", Matchers.hasItems(1, 2, 3)))
            .andExpect(jsonPath("$.data[?(@.id==1)].name").value(Matchers.contains("General Practitioner")))
            .andExpect(jsonPath("$.data[?(@.id==2)].name").value(Matchers.contains("Cardiologist")))
            .andExpect(jsonPath("$.data[?(@.id==3)].name").value(Matchers.contains("Diagnostician")));
    }

    @Test
    void shouldCreateSpecializationWhenInputIsValid() throws Exception {
        long createdSpecializationId = createValidSpecialization();
        int createdIdInt = Math.toIntExact(createdSpecializationId);

        mockMvc
            .perform(get("/specializations/{id}", createdSpecializationId).header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.name").value("Test Specialization"))
            .andExpect(jsonPath("$.data.description").value("Test description"))
            .andExpect(jsonPath("$.data.services[*].id", Matchers.containsInAnyOrder(1, 3)));

        mockMvc.perform(get("/services/{id}", 1).header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.specializations[*].id", Matchers.hasItem(createdIdInt)));

        mockMvc.perform(get("/services/{id}", 3).header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.specializations[*].id", Matchers.hasItem(createdIdInt)));

        mockMvc
            .perform(
                delete("/specializations/{id}", createdSpecializationId).header("Authorization", "Bearer " + token))
            .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn400WhenValidationErrorsPresentWithCreate() throws Exception {
        SpecializationCreateDto dto = new SpecializationCreateDto("A", "", List.of());
        String json = objectMapper.writeValueAsString(dto);

        mockMvc
            .perform(post("/specializations").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error.errorCode").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.error.message", Matchers.notNullValue()));
    }

    @Test
    void shouldDeleteSpecializationByIdWhenInputIsValid() throws Exception {
        long specializationId = createValidSpecialization();

        mockMvc.perform(delete("/specializations/{id}", specializationId).header("Authorization", "Bearer " + token))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/specializations/{id}", specializationId).header("Authorization", "Bearer " + token))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error.message").value("Specialization not found"));
    }

    @Test
    void shouldReturn404WhenSpecializationForDeleteNotFound() throws Exception {
        long notExistingId = 999L;

        mockMvc.perform(delete("/specializations/{id}", notExistingId).header("Authorization", "Bearer " + token))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error.message").value("Specialization not found"));
    }

    @Test
    void shouldUpdateSpecializationWhenInputIsValid() throws Exception {
        long specializationId = createValidSpecialization();
        int specializationIdInt = Math.toIntExact(specializationId);

        SpecializationCreateDto updateDto = new SpecializationCreateDto("Updated Name", "Updated description",
            List.of(2));
        String updateJson = objectMapper.writeValueAsString(updateDto);

        mockMvc
            .perform(put("/specializations/{id}", specializationId).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").value(specializationIdInt));

        mockMvc.perform(get("/specializations/{id}", specializationId).header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.name").value("Updated Name"))
            .andExpect(jsonPath("$.data.description").value("Updated description"))
            .andExpect(jsonPath("$.data.services[*].id", Matchers.containsInAnyOrder(2)));

        mockMvc.perform(get("/services/{id}", 1).header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.specializations[*].id", Matchers.not(Matchers.hasItem(specializationIdInt))));

        mockMvc.perform(get("/services/{id}", 3).header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.specializations[*].id", Matchers.not(Matchers.hasItem(specializationIdInt))));

        mockMvc.perform(get("/services/{id}", 2).header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.specializations[*].id", Matchers.hasItem(specializationIdInt)));

        mockMvc.perform(delete("/specializations/{id}", specializationId).header("Authorization", "Bearer " + token))
            .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenSpecializationForUpdateNotFound() throws Exception {
        long notExistingId = 999L;

        SpecializationCreateDto dto = new SpecializationCreateDto("Test Specialization", "Test description",
            List.of(1, 3));
        String json = objectMapper.writeValueAsString(dto);

        mockMvc
            .perform(put("/specializations/{id}", notExistingId).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error.message").value("Specialization not found"));
    }

    @Test
    void shouldReturn400WhenValidationErrorsPresentWithUpdate() throws Exception {
        long specializationId = createValidSpecialization();

        SpecializationCreateDto invalidDto = new SpecializationCreateDto("A", "", List.of());
        String invalidJson = objectMapper.writeValueAsString(invalidDto);

        mockMvc
            .perform(put("/specializations/{id}", specializationId).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error.errorCode").value("VALIDATION_ERROR"));

        mockMvc.perform(delete("/specializations/{id}", specializationId).header("Authorization", "Bearer " + token))
            .andExpect(status().isNoContent());
    }

    private long createValidSpecialization() throws Exception {
        SpecializationCreateDto createDto = new SpecializationCreateDto("Test Specialization", "Test description",
            List.of(1, 3));

        String createJson = objectMapper.writeValueAsString(createDto);

        String createResponse = mockMvc
            .perform(post("/specializations").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        return ((Number) JsonPath.read(createResponse, "$.data")).longValue();
    }

}
