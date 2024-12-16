package com.utn.interactiveconsortium.repository;

import com.utn.interactiveconsortium.entity.IssueReportEntity;
import com.utn.interactiveconsortium.enums.EIssueReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IssueReportRepository extends JpaRepository<IssueReportEntity,Long> {

    @Query("SELECT ir " +
            "FROM IssueReportEntity ir " +
            "WHERE ir.consortium.consortiumId = :consortiumId " +
            "AND (:status is null OR ir.status = :status)")
    Page<IssueReportEntity> getIssueReport(Long consortiumId, EIssueReportStatus status, Pageable pageable);

    @Query("SELECT ir " +
            "FROM IssueReportEntity ir " +
            "WHERE ir.consortium.consortiumId = :consortiumId " +
            "AND ir.person.personId = :personId " )
    Page<IssueReportEntity> getIssueReportPerson(Long consortiumId, Long personId, Pageable pageable);

    List<IssueReportEntity> findByConsortiumConsortiumId(Long consortiumId);

    List<IssueReportEntity> findByConsortiumConsortiumIdAndPersonPersonId(Long consortiumId, Long personId);

}

