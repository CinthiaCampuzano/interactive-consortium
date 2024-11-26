package com.utn.interactiveconsortium.service;

import com.utn.interactiveconsortium.adapter.AppUserAdapter;
import com.utn.interactiveconsortium.entity.AppUser;
import com.utn.interactiveconsortium.enums.ERole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public String getToken(AppUser user) {
        return getToken(new HashMap<>(), user);
    }

    private String getToken(Map<String, Object> extraClaims, AppUser user) {

        ERole userRole = ERole.valueOf(user.getAuthority());
        extraClaims.put("role", List.of(userRole));

        if (userRole == ERole.ROLE_PROPIETARY || userRole == ERole.ROLE_RESIDENT) {
            List<Long> consortiumIds = List.of(1L, 2L, 3L);
            List<Long> departmentIds = List.of(1L, 2L, 3L);
            String name = user.getPerson().getName();
            String lastName = user.getPerson().getLastName();
            String dni = user.getPerson().getDni();

            extraClaims.put("consortiumIds", consortiumIds);
            extraClaims.put("departmentIds", departmentIds);
            extraClaims.put("name", name);
            extraClaims.put("lastName", lastName);
            extraClaims.put("dni", dni);

        }

        if (userRole == ERole.ROLE_ADMIN) {
            List<Long> consortiumIds = List.of(1L, 2L, 3L);
            String name = user.getAdministrator().getName();
            String lastName = user.getAdministrator().getLastName();
            String mail = user.getAdministrator().getMail();
            String dni = user.getAdministrator().getDni();

            extraClaims.put("consortiumIds", consortiumIds);
            extraClaims.put("name", name);
            extraClaims.put("lastName", lastName);
            extraClaims.put("mail", mail);
            extraClaims.put("dni", dni);
        }

        return Jwts.builder()
                .claims(extraClaims)
                .claim("userId", user.getAppUserId())
//                .claim("consortiumIds", consortiumIds)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        return  username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T getClaim(String token, Function<Claims, T> claimResolver) {
        Claims claims = getAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }
}
