package com.capstone.safeGuard.repository;

import com.capstone.safeGuard.domain.Emergency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmergencyRepository extends JpaRepository<Emergency, Long> {

}
