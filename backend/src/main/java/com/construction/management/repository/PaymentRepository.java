package com.construction.management.repository;

import com.construction.management.entity.Payment;
import com.construction.management.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findBySiteId(Long siteId);

    List<Payment> findByWorkerId(Long workerId);

    List<Payment> findBySiteIdAndWorkerId(Long siteId, Long workerId);

    List<Payment> findBySiteIdAndCategoryId(Long siteId, Long categoryId);

    List<Payment> findBySiteIdAndStatus(Long siteId, PaymentStatus status);

    List<Payment> findBySiteIdAndEntryDateBetween(Long siteId, LocalDate startDate, LocalDate endDate);
}
