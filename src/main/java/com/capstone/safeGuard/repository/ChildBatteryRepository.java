package com.capstone.safeGuard.repository;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.ChildBattery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChildBatteryRepository extends JpaRepository<ChildBattery, Long> {
    Optional<ChildBattery> findByChildName(Child childName);
}
