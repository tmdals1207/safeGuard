package com.capstone.safeGuard.repository;

import com.capstone.safeGuard.domain.Helping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HelpingRepository extends JpaRepository<Helping, Long> {
    
    void delete(Helping helping);

    Helping findByHelperName(String helperName);
}
