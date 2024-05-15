package com.utn.interactiveconsortium.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.utn.interactiveconsortium.entity.AppUser;
import com.utn.interactiveconsortium.repository.AppUserRepository;

@RestController
public class DemoController {

   private final AppUserRepository repository;
   private final PasswordEncoder passwordEncoder;

   public DemoController(AppUserRepository repository, PasswordEncoder passwordEncoder) {
      this.repository = repository;
      this.passwordEncoder = passwordEncoder;
   }

   //add security to this endpoint only with role "ADMIN"
   @PostMapping(path = "/register")
   public String register(@RequestBody RegistrationRequest request) {
      var user = new AppUser();
      user.setUsername(request.username());
      user.setPassword(passwordEncoder.encode(request.password()));
      user.setAuthority(request.authority());

      repository.save(user);

      return "New user successfully registered";
   }

   @GetMapping(path = "/test")
   public String test() {
      return "Access to '/test' granted";
   }

   record RegistrationRequest(String username, String password, String authority) { }
}
