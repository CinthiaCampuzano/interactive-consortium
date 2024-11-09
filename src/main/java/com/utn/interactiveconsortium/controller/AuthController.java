package com.utn.interactiveconsortium.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utn.interactiveconsortium.config.JwtTokenProvider;
import com.utn.interactiveconsortium.dto.JwtResponse;
import com.utn.interactiveconsortium.dto.LoginRequest;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

   private final AuthenticationManager authenticationManager;

   private final JwtTokenProvider jwtTokenProvider;

   @PostMapping("/login")
   public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
      try {
         Authentication authentication = authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

         SecurityContextHolder.getContext().setAuthentication(authentication);
         String token = jwtTokenProvider.generateToken(authentication);

         return ResponseEntity.ok(new JwtResponse(token));
      } catch (Exception e) {
         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
      }
   }
}
