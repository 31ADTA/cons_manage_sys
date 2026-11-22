package com.construction.management.repository;

import com.construction.management.entity.Attendance;
import com.construction.management.entity.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findBySiteId(Long siteId);

    List<Attendance> findBySiteIdAndDate(Long siteId, LocalDate date);

    List<Attendance> findByWorkerId(Long workerId);

    List<Attendance> findByWorkerIdAndDateBetween(Long workerId, LocalDate startDate, LocalDate endDate);

    List<Attendance> findBySiteIdAndDateBetween(Long siteId, LocalDate startDate, LocalDate endDate);

    List<Attendance> findBySiteIdAndDateAndStatus(Long siteId, LocalDate date, AttendanceStatus status);

    Optional<Attendance> findBySiteIdAndWorkerIdAndDate(Long siteId, Long workerId, LocalDate date);
}
