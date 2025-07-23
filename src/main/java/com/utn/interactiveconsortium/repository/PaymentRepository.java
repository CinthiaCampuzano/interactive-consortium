package com.utn.interactiveconsortium.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.utn.interactiveconsortium.entity.PaymentEntity;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

}
