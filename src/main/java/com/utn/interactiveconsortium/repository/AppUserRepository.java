package com.utn.interactiveconsortium.repository;

import com.utn.interactiveconsortium.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findAppUserByUsername(String username);

    void deleteByUsername(String username);
}