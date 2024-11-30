package com.utn.interactiveconsortium.config;

import com.utn.interactiveconsortium.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);

        if(token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        final String username = jwtService.getUsernameFromToken(token);
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = userDetailsService.loadUserByUsername(username);
            if(jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // Set consortiumIds in the SecurityContext
//                List<Long> consortiumIds = jwtUtil.extractConsortiumIds(jwt);
//                request.setAttribute("consortiumIds", consortiumIds);
                List<Long> consortiumIds = jwtService.extractConsortiumIds(token);
                List<Long> propietaryDepartmentIds = jwtService.extractPropietaryDepartmentsIds(token);
                List<Long> residentDepartmentIds = jwtService.extractResidentDepartmentsIds(token);
                Map<String, List<Long>> details = Map.of(
                        JwtService.TOKEN_CONSORTIUM_IDS, consortiumIds,
                        JwtService.TOKEN_PROPIETARY_DEPARTMENT_IDS, propietaryDepartmentIds,
                        JwtService.TOKEN_RESIDENT_DEPARTMENT_IDS, residentDepartmentIds
                );
                authToken.setDetails(details);

            }
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

}
