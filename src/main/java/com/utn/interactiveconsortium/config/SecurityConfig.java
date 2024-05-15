package com.utn.interactiveconsortium.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {


   @Bean
   public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
   }

   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      return http
            .httpBasic(Customizer.withDefaults())
            .csrf(csrfConfigurer -> csrfConfigurer.disable())  // for POST requests via Postman
            .authorizeHttpRequests(auth -> auth
                  .requestMatchers(HttpMethod.POST, "/register").permitAll()
                  .requestMatchers(HttpMethod.GET, "/test").hasRole("USER")
                  .anyRequest().permitAll())
            .build();
   }

}
