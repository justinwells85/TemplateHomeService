package com.example.templatehomeservice.integration;

import com.example.templatehomeservice.model.User;
import com.example.templatehomeservice.model.dto.UserRequest;
import com.example.templatehomeservice.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateUser() throws Exception {
        UserRequest request = new UserRequest("johndoe", "john@example.com", "John", "Doe");

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("johndoe")))
                .andExpect(jsonPath("$.email", is("john@example.com")))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setFirstName("User");
        user1.setLastName("One");
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setFirstName("User");
        user2.setLastName("Two");
        userRepository.save(user2);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username", is("user1")))
                .andExpect(jsonPath("$[1].username", is("user2")));
    }

    @Test
    void shouldGetUserById() throws Exception {
        User user = new User();
        user.setUsername("johndoe");
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        User savedUser = userRepository.save(user);

        mockMvc.perform(get("/api/v1/users/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedUser.getId().intValue())))
                .andExpect(jsonPath("$.username", is("johndoe")))
                .andExpect(jsonPath("$.email", is("john@example.com")));
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("User not found")));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        User user = new User();
        user.setUsername("johndoe");
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        User savedUser = userRepository.save(user);

        UserRequest updateRequest = new UserRequest("johndoe", "newemail@example.com", "John", "Smith");

        mockMvc.perform(put("/api/v1/users/" + savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("newemail@example.com")))
                .andExpect(jsonPath("$.lastName", is("Smith")));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        User user = new User();
        user.setUsername("johndoe");
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        User savedUser = userRepository.save(user);

        mockMvc.perform(delete("/api/v1/users/" + savedUser.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/users/" + savedUser.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnValidationErrorWhenUsernameIsBlank() throws Exception {
        UserRequest request = new UserRequest("", "john@example.com", "John", "Doe");

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.errors.username", notNullValue()));
    }

    @Test
    void shouldReturnConflictWhenUsernameAlreadyExists() throws Exception {
        User user = new User();
        user.setUsername("johndoe");
        user.setEmail("existing@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        userRepository.save(user);

        UserRequest request = new UserRequest("johndoe", "new@example.com", "Jane", "Doe");

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Username already exists")));
    }
}
