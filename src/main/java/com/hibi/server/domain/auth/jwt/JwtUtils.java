package com.hibi.server.domain.auth.jwt;

import com.hibi.server.domain.auth.dto.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private int jwtRefreshExpiration;

    public LocalDateTime getRefreshTokenExpiryDate() {
        long expiryTimeMillis = new Date().getTime() + jwtRefreshExpiration;
        Date expiryDate = new Date(expiryTimeMillis);

        return expiryDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public String generateJwtToken(UserDetails userPrincipal, int jwtExpirationMs) {
        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAccessToken(Authentication authentication) {
        UserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();
        return generateJwtToken(userPrincipal, jwtExpiration);
    }

    public String generateRefreshToken(Authentication authentication) {
        UserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();
        return generateJwtToken(userPrincipal, jwtRefreshExpiration);
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getEmailFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}