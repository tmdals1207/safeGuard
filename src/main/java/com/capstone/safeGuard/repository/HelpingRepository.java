package com.capstone.safeGuard.repository;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Helping;
import com.capstone.safeGuard.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HelpingRepository extends JpaRepository<Helping, Long> {
    
    void delete(Helping helping);

    Helping findByHelper(Member helper);

    Helping findByHelper_MemberIdAndChild_ChildName(String memberId, String childName);
}
