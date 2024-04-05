package com.capstone.safeGuard.repository;

import com.capstone.safeGuard.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findById(String  id);
    Optional<Member> findByName(String name);
    Optional<Member> findByPassword(String password);

    List<Member> findAll();

    Member findBymemberIdAndPassword(String memberId, String password);
}

