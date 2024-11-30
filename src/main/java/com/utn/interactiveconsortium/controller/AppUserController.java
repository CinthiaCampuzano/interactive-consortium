package com.utn.interactiveconsortium.controller;

import com.utn.interactiveconsortium.dto.AppUserDto;
import com.utn.interactiveconsortium.dto.AuthResponseDto;
import com.utn.interactiveconsortium.service.AppUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/appUser")
public class AppUserController {

    private final AppUserDetailsService userDetailsService;

    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AppUserDto request) {
        return ResponseEntity.ok(userDetailsService.login(request));
    }

    //TODO reset password

}
