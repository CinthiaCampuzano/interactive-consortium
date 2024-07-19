package com.utn.interactiveconsortium.repository;

import com.utn.interactiveconsortium.entity.PersonEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonRepository extends JpaRepository<PersonEntity, Long> {

    @Query (value = "SELECT p FROM PersonEntity p " +
            "WHERE (:name IS NULL OR p.name = :name) " +
            "AND (:last_name IS NULL OR p.lastName = :last_name) " +
            "AND (:mail IS NULL OR p.mail = :mail) " +
            "AND (:dni IS NULL OR p.dni = :dni)")
    Page<PersonEntity> findPersonByFilters(
            @Param("name") String name,
            @Param("last_name") String lastName,
            @Param("mail") String mail,
            @Param("dni") String dni,
            Pageable pageable);

    boolean existsByDni(@Param("dni") String dni);
    boolean existsByMail(@Param("mail") String mail);
}
