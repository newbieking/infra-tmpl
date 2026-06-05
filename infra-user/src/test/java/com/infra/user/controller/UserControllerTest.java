package com.infra.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infra.common.config.GlobalExceptionHandler;
import com.infra.common.dto.UserDto;
import com.infra.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp(@Autowired UserController controller) {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .build();
    }

    @Test
    void createShouldReturn201() throws Exception {
        when(userService.create(any())).thenReturn(new UserDto(1L, "alice", "a@b.com", null, null));

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserDto(null, "alice", "a@b.com", null, null))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createWithBlankUsernameShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\",\"email\":\"a@b.com\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithInvalidEmailShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"email\":\"not-email\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllShouldReturnList() throws Exception {
        when(userService.findAll()).thenReturn(List.of(
                new UserDto(1L, "alice", "a@b.com", null, null)));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("alice"));
    }

    @Test
    void findByIdShouldReturnUser() throws Exception {
        when(userService.findById(1L)).thenReturn(new UserDto(1L, "alice", "a@b.com", null, null));

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    void findByIdNotFoundShouldReturn400() throws Exception {
        when(userService.findById(99L)).thenThrow(new IllegalArgumentException("User not found: 99"));

        mockMvc.perform(get("/api/v1/users/99"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isNoContent());
    }
}
