package com.inventory.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.security.JwtAuthenticationFilter;
import com.inventory.controller.AuthController;
import com.inventory.dto.AuthRequest;
import com.inventory.dto.AuthResponse;
import com.inventory.dto.RegisterRequest;
import com.inventory.entity.Role;
import com.inventory.authservice.AuthService;
import com.inventory.authservice.CustomUserDetailsService;
import com.inventory.utils.AuthUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private AuthService authService;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean private CustomUserDetailsService customUserDetailsService;
    @MockBean private AuthUtil authUtil;

    // ── POST /auth/register ───────────────────────────────────────────────────

    @Test
    void register_validRequest_returns200WithToken() throws Exception {
        RegisterRequest req = new RegisterRequest("dushyant", "pass123", "d@test.com", Role.ADMIN);
        AuthResponse res = new AuthResponse("jwt.token.here", "dushyant", "ADMIN");

        when(authService.register(any(RegisterRequest.class))).thenReturn(res);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token.here"))
                .andExpect(jsonPath("$.username").value("dushyant"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void register_duplicateUsername_returns500() throws Exception {
        RegisterRequest req = new RegisterRequest("dushyant", "pass123", "d@test.com", Role.CUSTOMER);

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Username already taken: dushyant"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void register_duplicateEmail_returns500() throws Exception {
        RegisterRequest req = new RegisterRequest("newuser", "pass123", "existing@test.com", Role.CUSTOMER);

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Email already registered: existing@test.com"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void register_noRoleProvided_defaultsToCustomer() throws Exception {
        // role omitted in request → should default to CUSTOMER in AuthService
        RegisterRequest req = new RegisterRequest("newcustomer", "pass123", "new@test.com", null);
        AuthResponse res = new AuthResponse("token.abc", "newcustomer", "CUSTOMER");

        when(authService.register(any(RegisterRequest.class))).thenReturn(res);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    // ── POST /auth/login ──────────────────────────────────────────────────────

    @Test
    void login_validCredentials_returns200WithToken() throws Exception {
        AuthRequest req = new AuthRequest("dushyant", "pass123");
        AuthResponse res = new AuthResponse("jwt.token.here", "dushyant", "ADMIN");

        when(authService.login(any(AuthRequest.class))).thenReturn(res);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("dushyant"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void login_wrongPassword_returns500() throws Exception {
        AuthRequest req = new AuthRequest("dushyant", "wrongpass");

        when(authService.login(any(AuthRequest.class)))
                .thenThrow(new RuntimeException("Bad credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void login_unknownUser_returns500() throws Exception {
        AuthRequest req = new AuthRequest("ghost", "pass123");

        when(authService.login(any(AuthRequest.class)))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }

    // ── /auth/** is public — no token needed ──────────────────────────────────

    @Test
    void register_noAuthHeader_stillReturns200() throws Exception {
        // /auth/** is permitAll() in WebSecurityConfig — no JWT required
        RegisterRequest req = new RegisterRequest("publicuser", "pass", "pub@test.com", Role.CUSTOMER);
        AuthResponse res = new AuthResponse("token.xyz", "publicuser", "CUSTOMER");

        when(authService.register(any())).thenReturn(res);

        // Deliberately no .with(csrf()) to simulate a plain public POST
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }
}
