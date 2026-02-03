package com.example.FinanceManagementApp.security;

import com.example.FinanceManagementApp.model.entity.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // ENV veya application.properties'den gelmeli (şimdilik sabit)
    private final String secretKey = "financeApplicationSecretKey123456789012345";

    private final long accessTokenExpiration = 1000 * 60 * 5; //5dk

    //setSubject() = Token’ın kime ait olduğunu tanımlayan ana kimlik alanıdır (principal)   subject=mail

    public String generateAccessToken(Users user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getKey())
                .compact();
    }



    private Key getKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public Long extractUserId(String token) {

        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }


    public String extractEmail(String token) {
        // extract the email from jwt token
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public boolean isTokenValid(String token) {

        try {
            extractAllClaims(token); // signature + structure validate eder
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }


    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
