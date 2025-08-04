package com.base.infra.config.security.service;

import com.base.domain.user.domain.Account;
import com.base.infra.config.security.JwtConfig;
import com.base.infra.config.security.TokenInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final JwtConfig jwtConfig;

    public String generateToken(Account account) {
        return generateToken(account, new HashMap<>());
    }

    public String generateToken(Account account, Map<String, Object> extraClaims) {
        return buildToken(account, extraClaims, jwtConfig.getExpiration());
    }

    public String generateRefreshToken(Account account) {
        return buildToken(account, new HashMap<>(), jwtConfig.getExpiration() * 7); // 7 times longer for refresh
    }

    private String buildToken(Account account, Map<String, Object> extraClaims, Long expiration) {
        Instant now = Instant.now();
        Instant expirationTime = now.plusMillis(expiration);

        Map<String, Object> claims = new HashMap<>(extraClaims);
        claims.put("userId", account.getUserId());
        claims.put("role", account.getRole().name());
        claims.put("authorities", account.getAuthorities());

        RSAPrivateKey privateKey = jwtConfig.getRsaPrivateKey();
        if (privateKey == null) {
            throw new IllegalStateException("RSA Private Key not configured");
        }

        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(account.getUsername())
                    .setIssuer(jwtConfig.getIssuer())
                    .setAudience(jwtConfig.getAudience())
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(expirationTime))
                    .signWith(privateKey, SignatureAlgorithm.RS256) // Sử dụng RS256 cho RSA
                    .compact();
        } catch (Exception e) {
            log.error("Error generating JWT token", e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    public boolean isTokenValid(String token, Account account) {
        try {
            final String username = extractUsername(token);
            return (username.equals(account.getUsername())) && !isTokenExpired(token);
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            log.warn("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", String.class));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        RSAPublicKey publicKey = jwtConfig.getRsaPublicKey();
        if (publicKey == null) {
            throw new IllegalStateException("RSA Public Key not configured");
        }

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .requireIssuer(jwtConfig.getIssuer())
                    .requireAudience(jwtConfig.getAudience())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.warn("JWT parsing failed: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    public boolean validateToken(String token, Account account) {
        try {
            return isTokenValid(token, account);
        } catch (Exception e) {
            log.warn("Token validation error: {}", e.getMessage());
            return false;
        }
    }

    public boolean validateTokenStructure(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            log.warn("Token structure validation failed: {}", e.getMessage());
            return false;
        }
    }

    public Long getExpirationTime() {
        return jwtConfig.getExpiration();
    }

    public String getIssuer() {
        return jwtConfig.getIssuer();
    }

    public String getAudience() {
        return jwtConfig.getAudience();
    }

    public TokenInfo extractTokenInfo(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return TokenInfo.builder()
                    .username(claims.getSubject())
                    .userId(claims.get("userId", String.class))
                    .role(claims.get("role", String.class))
                    .issuer(claims.getIssuer())
                    .audience(claims.getAudience())
                    .issuedAt(claims.getIssuedAt())
                    .expiresAt(claims.getExpiration())
                    .build();
        } catch (Exception e) {
            log.warn("Error extracting token info: {}", e.getMessage());
            throw new RuntimeException("Failed to extract token information", e);
        }
    }
}