package io.github.emrebaykusak.refreshtoken.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenManagerTest {

    private final TokenManager tokenManager = new TokenManager("FKCzLIlF2ndCBOG7rs9l5qSWR8jFWJ4HIAZ3b9Fhqlk=");

    @Test
    @DisplayName("Generated refresh token is recognized as refresh token")
    void generatedRefreshTokenIsRecognized() {
        String token = tokenManager.generateRefreshToken("alice");

        assertTrue(tokenManager.tokenValidate(token));
        assertTrue(tokenManager.isRefreshToken(token));
        assertFalse(tokenManager.isAccessToken(token));
        assertEquals("alice", tokenManager.extractUsername(token));
    }

    @Test
    @DisplayName("Generated access token is not recognized as refresh token")
    void generatedAccessTokenIsNotRefreshToken() {
        String token = tokenManager.generateAccessToken("alice");

        assertTrue(tokenManager.tokenValidate(token));
        assertFalse(tokenManager.isRefreshToken(token));
        assertTrue(tokenManager.isAccessToken(token));
        assertEquals("alice", tokenManager.extractUsername(token));
    }
}