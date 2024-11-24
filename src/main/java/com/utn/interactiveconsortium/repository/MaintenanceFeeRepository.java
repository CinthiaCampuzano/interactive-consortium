package com.utn.interactiveconsortium.repository;

import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import com.utn.interactiveconsortium.entity.MaintenanceFeeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface MaintenanceFeeRepository extends JpaRepository<MaintenanceFeeEntity, Long> {

    Optional<MaintenanceFeeEntity> findByConsortiumAndPeriod(ConsortiumEntity consortium, LocalDate period);

    Page<MaintenanceFeeEntity> findByConsortiumConsortiumIdOrderByPeriodDesc(Long consortiumId, Pageable pageable);

}
