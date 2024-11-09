package com.utn.interactiveconsortium.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.utn.interactiveconsortium.service.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebConfig {

   private final JwtAuthenticationEntryPoint unauthorizedHandler;
   private final JwtTokenProvider jwtTokenProvider;
   private final CustomUserDetailsService customUserDetailsService;

   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      http
            .cors(withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(unauthorizedHandler))
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                  .requestMatchers("/auth/**").permitAll()
                  .requestMatchers("/administrators/**").hasRole("ADMIN")
                  .anyRequest().authenticated())
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

      return http.build();
   }

   @Bean
   public JwtAuthenticationFilter jwtAuthenticationFilter() {
      return new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService);
   }

   @Bean
   public PasswordEncoder passwordEncoder() {
      return NoOpPasswordEncoder.getInstance(); // No realiza cifrado
   }

   @Bean
   public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
      return authenticationConfiguration.getAuthenticationManager();
   }
}
