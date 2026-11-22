package com.construction.management.repository;

import com.construction.management.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {

    Optional<Unit> findByNameIgnoreCase(String name);

    Optional<Unit> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);
}
