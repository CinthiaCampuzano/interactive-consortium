package com.utn.interactiveconsortium.config;

import com.utn.interactiveconsortium.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).build().parseClaimsJws(token).getBody();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public List<Long> extractConsortiumIds(String token) {
        return extractAllClaims(token).get(JwtService.TOKEN_CONSORTIUM_IDS, List.class);
    }
}