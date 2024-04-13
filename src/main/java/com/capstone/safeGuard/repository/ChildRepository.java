package com.capstone.safeGuard.repository;

import com.capstone.safeGuard.domain.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {
    public Child findBychildName(String name); // child_name 사용


    void delete(Optional<Child> findChild);
}
