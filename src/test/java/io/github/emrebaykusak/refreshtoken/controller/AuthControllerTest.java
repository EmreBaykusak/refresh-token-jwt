package io.github.emrebaykusak.refreshtoken.controller;

import io.github.emrebaykusak.refreshtoken.auth.TokenManager;
import io.github.emrebaykusak.refreshtoken.security.WebSecurityConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(WebSecurityConfiguration.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenManager tokenManager;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @Test
    @DisplayName("/login returns access and refresh tokens")
    void loginReturnsTokens() throws Exception {
        doReturn("access-token-123").when(tokenManager).generateAccessToken("alice");
        doReturn("refresh-token-123").when(tokenManager).generateRefreshToken("alice");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "alice",
                                  "password": "secret"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token-123"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-123"));
    }

    @Test
    @DisplayName("/refresh returns new tokens for a valid refresh token")
    void refreshReturnsNewTokens() throws Exception {
        doReturn(true).when(tokenManager).tokenValidate("valid-refresh-token");
        doReturn(true).when(tokenManager).isRefreshToken("valid-refresh-token");
        doReturn("alice").when(tokenManager).extractUsername("valid-refresh-token");
        doReturn("new-access-token").when(tokenManager).generateAccessToken("alice");
        doReturn("new-refresh-token").when(tokenManager).generateRefreshToken("alice");

        mockMvc.perform(post("/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": "valid-refresh-token"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"));
    }

    @Test
    @DisplayName("/refresh returns 401 for invalid token")
    void refreshReturns401ForInvalidToken() throws Exception {
        doReturn(false).when(tokenManager).tokenValidate("invalid-token");

        mockMvc.perform(post("/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": "invalid-token"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("/refresh returns 401 when token is not a refresh token")
    void refreshReturns401ForAccessToken() throws Exception {
        doReturn(true).when(tokenManager).tokenValidate("access-token");
        doReturn(false).when(tokenManager).isRefreshToken("access-token");

        mockMvc.perform(post("/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": "access-token"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("/login returns error when authentication fails")
    void loginReturnsErrorOnBadCredentials() throws Exception {
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "alice",
                                  "password": "wrong"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }
}