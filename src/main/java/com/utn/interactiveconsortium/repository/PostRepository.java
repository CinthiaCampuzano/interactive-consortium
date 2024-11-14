package com.utn.interactiveconsortium.repository;

import com.utn.interactiveconsortium.entity.AmenityEntity;
import com.utn.interactiveconsortium.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

    @Query("SELECT ps FROM PostEntity ps WHERE ps.consortium.consortiumId = :consortiumId " +
            "AND lower(ps.title) LIKE lower(concat('%', :title,'%'))")
            Page<PostEntity> findByTitleContainingAndConsortiumId
            ( @Param("consortiumId") Long consortiumId,
              @Param("title") String title,
              Pageable pageable);

    Page<PostEntity> findByConsortiumConsortiumId(Long consortiumId, Pageable pageable);

}
