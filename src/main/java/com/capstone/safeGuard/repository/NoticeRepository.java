package com.capstone.safeGuard.repository;

import com.capstone.safeGuard.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice,Long> {
}
