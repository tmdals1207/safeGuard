package com.capstone.safeGuard.repository;

import com.capstone.safeGuard.domain.TestDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<TestDomain, Integer> {
    Optional<TestDomain> findById(int id);
    Optional<TestDomain> findByName(String name);
    List<TestDomain> findAll();
}
