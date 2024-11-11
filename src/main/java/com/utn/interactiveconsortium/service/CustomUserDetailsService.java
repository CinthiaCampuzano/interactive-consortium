package com.utn.interactiveconsortium.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.utn.interactiveconsortium.entity.PersonEntity;
import com.utn.interactiveconsortium.repository.PersonRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

   @Autowired
   private PersonRepository personRepository;

   @Override
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      PersonEntity user = personRepository.findByMail(username).orElse(null);
      if (user == null) {
         throw new UsernameNotFoundException("Usuario no encontrado: " + username);
      }
      return new org.springframework.security.core.userdetails.User(user.getMail(), user.getPassword(), new ArrayList<>());
   }
}
