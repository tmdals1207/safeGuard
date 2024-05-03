package com.capstone.safeGuard.repository;

import com.capstone.safeGuard.domain.Emergency;
import com.capstone.safeGuard.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyRepository extends JpaRepository<Emergency, Long> {
    List<Emergency> findAllBySenderId(Member member);
}
