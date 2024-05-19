package com.capstone.safeGuard.repository;

import com.capstone.safeGuard.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {


    Member findByEmail(String loginEmail);

    boolean existsByEmail(String email);
    boolean existsByMemberId(String memberId);
}
