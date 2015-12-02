package com.payleven.inapp.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByExternalId(String orderNumber);

    List<Payment> findAllByTypeAndReference(Payment.Type charge, Long id);
}
