package com.utn.interactiveconsortium.repository;

import com.utn.interactiveconsortium.entity.PostEntity;
import com.utn.interactiveconsortium.entity.PostReactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostReactionRepository extends JpaRepository<PostReactionEntity, Long> {

    Optional<PostReactionEntity> findByPostPostIdAndPersonPersonId(Long postId, Long personId);

}
