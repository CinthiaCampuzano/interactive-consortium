package com.utn.interactiveconsortium.repository;

import com.utn.interactiveconsortium.entity.AdministratorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdministratorRepository extends JpaRepository<AdministratorEntity, Long> {


    @Query(value = "SELECT a FROM AdministratorEntity a " +
            "WHERE (:name IS NULL OR a.name = :name) " +
            "AND (:last_name IS NULL OR a.lastName = :last_name) " +
            "AND (:mail IS NULL OR a.mail = :mail) " +
            "AND (:dni IS NULL OR a.dni = :dni)")
    Page<AdministratorEntity> findAdministratorsByFilters(
            @Param("name") String name,
            @Param("last_name") String lastName,
            @Param("mail") String mail,
            @Param("dni") String dni,
            Pageable pageable);

    boolean existsByDni(@Param("dni") String dni);
    boolean existsByMail(@Param("mail") String mail);
}
