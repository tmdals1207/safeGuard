package com.capstone.safeGuard.repository;

import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.domain.MemberFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberFileRepository extends JpaRepository<MemberFile, Long> {
    Optional<MemberFile> findByMember(Member member);
}
