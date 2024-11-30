package com.utn.interactiveconsortium.config;

import com.utn.interactiveconsortium.entity.AppUser;
import com.utn.interactiveconsortium.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final AppUserRepository appUserRepository;

    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            if (appUserRepository.findByUsername("root").isEmpty()) {
                AppUser rootUser = new AppUser();
                rootUser.setUsername("root");
                rootUser.setPassword(passwordEncoder.encode("Proyecto2024"));
                rootUser.setAuthority("ROLE_ROOT");
                appUserRepository.save(rootUser);
            }
        };
    }
}