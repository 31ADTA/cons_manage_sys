package com.construction.management.repository;

import com.construction.management.entity.Material;
import com.construction.management.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    List<Material> findBySiteId(Long siteId);

    List<Material> findBySiteIdAndPaymentStatus(Long siteId, PaymentStatus paymentStatus);

    List<Material> findBySiteIdAndEntryDateBetween(Long siteId, LocalDate startDate, LocalDate endDate);

    List<Material> findByVendorId(Long vendorId);
}
