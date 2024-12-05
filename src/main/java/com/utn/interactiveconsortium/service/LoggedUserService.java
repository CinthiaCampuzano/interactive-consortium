package com.utn.interactiveconsortium.service;

import com.utn.interactiveconsortium.adapter.AppUserAdapter;
import com.utn.interactiveconsortium.entity.AdministratorEntity;
import com.utn.interactiveconsortium.entity.PersonEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LoggedUserService {

    public AdministratorEntity getLoggedAdministrator() {
        return ((AppUserAdapter) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAppUser().getAdministrator();
    }

    public PersonEntity getLoggedPerson() {
        return ((AppUserAdapter) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAppUser().getPerson();
    }

    public List<Long> getAssociatedConsortiumIds() {
        Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return (List<Long>) details.get(JwtService.TOKEN_CONSORTIUM_IDS);
    }

    public List<Long> getAssociatedPropietaryDepartmentIds() {
        Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return (List<Long>) details.get(JwtService.TOKEN_PROPIETARY_DEPARTMENT_IDS);
    }

    public List<Long> getAssociatedResidentDepartmentIds() {
        Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return (List<Long>) details.get(JwtService.TOKEN_RESIDENT_DEPARTMENT_IDS);
    }

}
