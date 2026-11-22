package com.construction.management.repository;

import com.construction.management.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {

    List<Worker> findBySiteId(Long siteId);

    List<Worker> findBySiteIdAndCategoryId(Long siteId, Long categoryId);

    List<Worker> findBySiteIdAndFullNameContainingIgnoreCase(Long siteId, String fullName);
}
