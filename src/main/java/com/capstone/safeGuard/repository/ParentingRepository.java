package com.capstone.safeGuard.repository;

import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.domain.Parenting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParentingRepository extends JpaRepository<Parenting, Long> {
    List<Parenting> findAllByParent(Member member);
}
