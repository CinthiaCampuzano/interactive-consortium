package com.utn.interactiveconsortium.repository;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

import com.utn.interactiveconsortium.entity.AppUser;

public interface AppUserRepository extends CrudRepository<AppUser, Integer> {
   Optional<AppUser> findAppUserByUsername(String username);
}
