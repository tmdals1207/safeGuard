package com.capstone.safeGuard.repository;

import com.capstone.safeGuard.domain.TestDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends JpaRepository<TestDomain, Integer> {
}
