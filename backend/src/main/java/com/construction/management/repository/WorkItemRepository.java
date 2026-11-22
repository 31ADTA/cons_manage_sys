package com.construction.management.repository;

import com.construction.management.entity.WorkItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkItemRepository extends JpaRepository<WorkItem, Long> {

    List<WorkItem> findBySiteId(Long siteId);

    List<WorkItem> findBySiteIdAndEntryDateBetween(Long siteId, LocalDate startDate, LocalDate endDate);

    List<WorkItem> findByVendorId(Long vendorId);
}
