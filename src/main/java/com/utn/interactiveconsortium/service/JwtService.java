package com.utn.interactiveconsortium.service;

import com.utn.interactiveconsortium.entity.AppUser;
import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import com.utn.interactiveconsortium.entity.DepartmentEntity;
import com.utn.interactiveconsortium.enums.ERole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public static final String TOKEN_CONSORTIUM_IDS = "consortiumIds";

    public static final String TOKEN_PROPIETARY_DEPARTMENT_IDS = "propietaryDepartmentIds";

    public static final String TOKEN_RESIDENT_DEPARTMENT_IDS = "residentDepartmentIds";

    public static final String TOKEN_NAME = "name";

    public static final String TOKEN_LAST_NAME = "lastName";

    public static final String TOKEN_DNI = "dni";

    public static final String TOKEN_MAIL = "mail";

    public static final String TOKEN_USER_ID = "userId";

    public String getToken(AppUser user) {
        return getToken(new HashMap<>(), user);
    }

    private String getToken(Map<String, Object> extraClaims, AppUser user) {

        ERole userRole = ERole.valueOf(user.getAuthority());
        extraClaims.put("role", List.of(userRole));

        if (userRole == ERole.ROLE_PROPIETARY || userRole == ERole.ROLE_RESIDENT) {
            List<Long> consortiumIds = user.getPerson()
                    .getConsortiums()
                    .stream()
                    .map(ConsortiumEntity::getConsortiumId)
                    .toList();

            List<Long> propietaryDepartmentIds = user.getPerson()
                    .getPropietaryDepartments()
                    .stream()
                    .map(DepartmentEntity::getDepartmentId)
                    .toList();

            List<Long> residentDepartmentIds = user.getPerson()
                    .getResidentDepartments()
                    .stream()
                    .map(DepartmentEntity::getDepartmentId)
                    .toList();

            List<ERole> userRoles= new ArrayList<>();
            if (!propietaryDepartmentIds.isEmpty()) {
                userRoles.add(ERole.ROLE_PROPIETARY);
            }

            if (!residentDepartmentIds.isEmpty()) {
                userRoles.add(ERole.ROLE_RESIDENT);
            }

            extraClaims.put("role", userRoles);

            String name = user.getPerson().getName();
            String lastName = user.getPerson().getLastName();
            String dni = user.getPerson().getDni();

            extraClaims.put(TOKEN_CONSORTIUM_IDS, consortiumIds);
            extraClaims.put(TOKEN_PROPIETARY_DEPARTMENT_IDS, propietaryDepartmentIds);
            extraClaims.put(TOKEN_RESIDENT_DEPARTMENT_IDS, residentDepartmentIds);
            extraClaims.put(TOKEN_NAME, name);
            extraClaims.put(TOKEN_LAST_NAME, lastName);
            extraClaims.put(TOKEN_DNI, dni);

        }

        if (userRole == ERole.ROLE_ADMIN) {
            List<Long> consortiumIds = user.getAdministrator()
                    .getConsortiums()
                    .stream()
                    .map(ConsortiumEntity::getConsortiumId)
                    .toList();
            String name = user.getAdministrator().getName();
            String lastName = user.getAdministrator().getLastName();
            String mail = user.getAdministrator().getMail();
            String dni = user.getAdministrator().getDni();

            extraClaims.put(TOKEN_CONSORTIUM_IDS, consortiumIds);
            extraClaims.put(TOKEN_NAME, name);
            extraClaims.put(TOKEN_LAST_NAME, lastName);
            extraClaims.put(TOKEN_DNI, dni);
            extraClaims.put(TOKEN_MAIL, mail);
        }

        return Jwts.builder()
                .claim(TOKEN_USER_ID, user.getAppUserId())
                .claims(extraClaims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        return  username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T getClaim(String token, Function<Claims, T> claimResolver) {
        Claims claims = getAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }

    public List<Long> extractConsortiumIds(String token) {
        List<Long> list = getAllClaims(token).get(JwtService.TOKEN_CONSORTIUM_IDS, List.class);
        return list != null ? list : Collections.emptyList();
    }

    public List<Long> extractPropietaryDepartmentsIds(String token) {
        List list = getAllClaims(token).get(JwtService.TOKEN_PROPIETARY_DEPARTMENT_IDS, List.class);
        return list != null ? list : Collections.emptyList();
    }

    public List<Long> extractResidentDepartmentsIds(String token) {
        List list = getAllClaims(token).get(JwtService.TOKEN_RESIDENT_DEPARTMENT_IDS, List.class);
        return list != null ? list : Collections.emptyList();
    }
}
