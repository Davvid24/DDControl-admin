package com.ddcontrol.ddcontroladmin.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails,
                                Integer idUsuario,
                                Integer idEmpresa,
                                String rol) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("idUsuario", idUsuario)
                .claim("idEmpresa", idEmpresa)
                .claim("rol", rol)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiration))
                .signWith(getKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public Integer extractIdUsuario(String token) {
        Object val = extractClaims(token).get("idUsuario");
        return val != null ? ((Number) val).intValue() : null;
    }

    public Integer extractIdEmpresa(String token) {
        Object val = extractClaims(token).get("idEmpresa");
        return val != null ? ((Number) val).intValue() : null;
    }

    public String extractRol(String token) {
        return (String) extractClaims(token).get("rol");
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String email = extractEmail(token);
            return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}