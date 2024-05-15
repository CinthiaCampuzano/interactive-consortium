package com.utn.interactiveconsortium.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.utn.interactiveconsortium.entity.AppUser;
import com.utn.interactiveconsortium.repository.AppUserRepository;
import com.utn.interactiveconsortium.security.AppUserAdapter;

@Service
public class AppUserDetailsServiceImpl implements UserDetailsService {
   private final AppUserRepository repository;

   public AppUserDetailsServiceImpl(AppUserRepository repository) {
      this.repository = repository;
   }

   @Override
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      AppUser user = repository
            .findAppUserByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Not found"));

      return new AppUserAdapter(user);
   }
}
