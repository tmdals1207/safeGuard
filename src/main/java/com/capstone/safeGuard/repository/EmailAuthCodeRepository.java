package com.capstone.safeGuard.repository;

import com.capstone.safeGuard.domain.EmailAuthCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailAuthCodeRepository extends JpaRepository<EmailAuthCode, String> {

}
