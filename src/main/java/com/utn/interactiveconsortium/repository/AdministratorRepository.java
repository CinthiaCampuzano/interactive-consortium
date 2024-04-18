package com.utn.interactiveconsortium.repository;

import com.utn.interactiveconsortium.entity.AdministratorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdministratorRepository extends JpaRepository<AdministratorEntity, Long> {

    @Query(value = "SELECT a FROM AdministratorEntity a WHERE LOWER(a.name) LIKE CONCAT('%', LOWER(:name), '%')")
    List<AdministratorEntity> findAByName(@Param("name") String name);

    boolean existsByDni(@Param("dni") String dni);
    boolean existsByMail(@Param("mail") String mail);
}
