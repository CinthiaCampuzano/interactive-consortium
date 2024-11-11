package com.utn.interactiveconsortium.config;

import java.util.Date;
import javax.crypto.SecretKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;  // Importante para generar claves seguras

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

   @Value("${jwt.secret}")
   private String jwtSecret;

   @Value("${jwt.expiration}")
   private int jwtExpiration;

   public String generateToken(Authentication authentication) {
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      Date now = new Date();
      Date expiryDate = new Date(now.getTime() + jwtExpiration);

      // Generar clave segura de 512 bits con la clase Keys
      SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());  // Esto genera una clave de 512 bits
      log.info("cuando creo el token:" + secretKey + " " +  jwtSecret );

      return Jwts.builder()
                 .setSubject(userDetails.getUsername())
                 .claim("roles", "ADMIN")
                 .setIssuedAt(now)
                 .setExpiration(expiryDate)
                 .signWith(secretKey, SignatureAlgorithm.HS512)
                 .compact();
   }

   public String getUsernameFromJWT(String token) {
      SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());  // Asegúrate de usar la misma clave para verificar
      Claims claims = Jwts.parser()
                          .setSigningKey(secretKey)
                          .parseClaimsJws(token)
                          .getBody();

      return claims.getSubject();
   }

   public boolean validateToken(String authToken) {
      try {
         SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());  // Usar la misma clave para verificar
         log.info("cuando valido el token:" + secretKey + jwtSecret);
         Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
         return true;
      } catch (JwtException | IllegalArgumentException ex) {
         return false;
      }
   }

   public String getRoleFromJWT(String token) {
      SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
      Claims claims = Jwts.parser()
                          .setSigningKey(secretKey)
                          .parseClaimsJws(token)
                          .getBody();
      return claims.get("roles", String.class);  // Extrae el rol "ADMIN" del token
   }

}
