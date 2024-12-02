package com.utn.interactiveconsortium.service;


import com.utn.interactiveconsortium.adapter.AppUserAdapter;
import com.utn.interactiveconsortium.dto.AppUserDto;
import com.utn.interactiveconsortium.dto.AuthResponseDto;
import com.utn.interactiveconsortium.entity.AdministratorEntity;
import com.utn.interactiveconsortium.entity.AppUser;
import com.utn.interactiveconsortium.enums.ERole;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = repository
                .findAppUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found"));

        return new AppUserAdapter(user);
    }

    public String register(AppUserDto request) {
        var user = new AppUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAuthority(request.getAuthority());

        repository.save(user);

        return "New user successfully registered";
    }

    public void register(AdministratorEntity administrator) {
        var user = new AppUser();
        user.setUsername(administrator.getMail());
        user.setPassword(passwordEncoder.encode(administrator.getDni()));
        user.setAuthority(ERole.ROLE_ADMIN.name());
        user.setAdministrator(administrator);

        repository.save(user);
    }

    public AppUser findByUsername(String username) throws EntityNotFoundException {
        return repository.findByUsername(username)
                                    .orElseThrow(() -> new EntityNotFoundException("No existe ese usuario"));
    }

    public AuthResponseDto login(AppUserDto request) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        AppUser user = ((AppUserAdapter) loadUserByUsername(request.getUsername())).getAppUser();
        return AuthResponseDto.builder()
                .token(jwtService.getToken(user))
                .build();
    }

    public void updateAppUser(AppUser appUser) {
        repository.save(appUser);
    }

    public void deleteByUsername(String mail) {
        repository.deleteByUsername(mail);
    }
}