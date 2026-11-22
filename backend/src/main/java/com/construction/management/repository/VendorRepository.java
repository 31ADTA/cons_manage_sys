package com.construction.management.repository;

import com.construction.management.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    List<Vendor> findByNameContainingIgnoreCase(String name);

    List<Vendor> findByPhone(String phone);
}
