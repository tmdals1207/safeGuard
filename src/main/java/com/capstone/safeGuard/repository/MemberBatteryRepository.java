package com.capstone.safeGuard.repository;

import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.domain.MemberBattery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberBatteryRepository extends JpaRepository<MemberBattery, Long> {
    Optional<MemberBattery> findByMemberId(Member member);

}
