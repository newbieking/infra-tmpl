package com.infra.template.controller;

import com.infra.template.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void illegalArgumentExceptionShouldReturn400() throws Exception {
        when(userService.findById(999L)).thenThrow(new IllegalArgumentException("User not found: 999"));

        mockMvc.perform(get("/api/v1/users/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User not found: 999"));
    }

    @Test
    void unexpectedExceptionShouldReturn500() throws Exception {
        when(userService.findById(1L)).thenThrow(new RuntimeException("unexpected"));

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal server error"));
    }
}
