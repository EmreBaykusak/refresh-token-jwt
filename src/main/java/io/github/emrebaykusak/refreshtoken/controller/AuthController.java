package io.github.emrebaykusak.refreshtoken.controller;

import io.github.emrebaykusak.refreshtoken.auth.TokenManager;
import io.github.emrebaykusak.refreshtoken.models.AuthResponse;
import io.github.emrebaykusak.refreshtoken.models.RefreshRequest;
import io.github.emrebaykusak.refreshtoken.models.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final TokenManager tokenManager;
    private final AuthenticationManager authenticationManager;

    public AuthController(TokenManager tokenManager, AuthenticationManager authenticationManager) {
        this.tokenManager = tokenManager;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        } catch (BadCredentialsException e){
            return ResponseEntity.status(401).build();
        }

        var username = loginRequest.username();
        var authResponse = new AuthResponse(
                tokenManager.generateAccessToken(username),
                tokenManager.generateRefreshToken(username));
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        String refreshToken = request.refreshToken();

        if (!tokenManager.tokenValidate(refreshToken) || !tokenManager.isRefreshToken(refreshToken)) {
            return ResponseEntity.status(401).build();
        }

        String username = tokenManager.extractUsername(refreshToken);
        AuthResponse response = new AuthResponse(
                tokenManager.generateAccessToken(username),
                tokenManager.generateRefreshToken(username)
        );

        return ResponseEntity.ok(response);
    }
}
