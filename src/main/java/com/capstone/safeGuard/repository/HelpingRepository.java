package com.capstone.safeGuard.repository;

import com.capstone.safeGuard.domain.Helping;
import com.capstone.safeGuard.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HelpingRepository extends JpaRepository<Helping, Long> {
    
    void delete(Helping helping);

    Helping findByHelperName(String helperName);

    List<Helping> findAllByHelper(Member foundMember);
}
