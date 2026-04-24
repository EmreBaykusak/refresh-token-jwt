package io.github.emrebaykusak.refreshtoken.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Objects;

@Service
public class TokenManager {
    private static final int accessValidity = 5 * 60 * 1000;
    private static final int refreshValidity = 7 * 24 * 60 * 60 * 1000;
    private final SecretKey key;

    public TokenManager(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateAccessToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuer("io.github.emrebaykusak")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessValidity))
                .claim("token_type", "access")
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuer("io.github.emrebaykusak")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshValidity))
                .claim("token_type", "refresh")
                .signWith(key)
                .compact();
    }

    public boolean tokenValidate(String token) {
        return extractUsername(token) != null && isNotExpired(token);
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isNotExpired(String token) {
        return getClaims(token).getExpiration().after(new Date(System.currentTimeMillis()));
    }

    public boolean isAccessToken(String token) {
        return Objects.equals(getClaims(token).get("token_type", String.class), "access");
    }

    public boolean isRefreshToken(String token) {
        return Objects.equals(getClaims(token).get("token_type", String.class), "refresh");
    }

    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
