package com.infra.auth.controller;

import com.infra.auth.entity.SysUser;
import com.infra.auth.service.AuthService;
import com.infra.auth.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
class AuthControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthService authService;

    @BeforeEach
    void setUp(@Autowired AuthController controller) {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void loginWithValidCredentialsShouldReturnToken() throws Exception {
        SysUser user = new SysUser("admin", "hashed", "ADMIN");
        user.setId(1L);
        when(authService.authenticate("admin", "admin123")).thenReturn(user);
        when(jwtService.generateToken(eq("1"), any(Map.class))).thenReturn("test-token");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void loginWithInvalidCredentialsShouldReturn401() throws Exception {
        when(authService.authenticate("wrong", "wrong"))
                .thenThrow(new IllegalArgumentException("用户不存在: wrong"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"wrong\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("用户不存在: wrong"));
    }

    @Test
    void registerShouldReturn201() throws Exception {
        SysUser user = new SysUser("newuser", "hashed", "USER");
        user.setId(2L);
        when(authService.register("newuser", "pass123", "USER")).thenReturn(user);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"newuser\",\"password\":\"pass123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(2))
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    void registerDuplicateShouldReturn400() throws Exception {
        when(authService.register("existing", "pass", "USER"))
                .thenThrow(new IllegalArgumentException("用户名已存在: existing"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"existing\",\"password\":\"pass\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void verifyValidTokenShouldReturnOk() throws Exception {
        when(jwtService.validateToken("valid-token")).thenReturn(true);
        when(jwtService.parseClaims("valid-token")).thenReturn(Map.of("sub", "1", "username", "admin"));

        mockMvc.perform(get("/api/v1/auth/verify")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    void verifyInvalidTokenShouldReturn401() throws Exception {
        when(jwtService.validateToken("bad-token")).thenReturn(false);

        mockMvc.perform(get("/api/v1/auth/verify")
                        .header("Authorization", "Bearer bad-token"))
                .andExpect(status().isUnauthorized());
    }
}
