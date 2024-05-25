package com.capstone.safeGuard.repository;

import com.capstone.safeGuard.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    ArrayList<Notice> findAllByReceiverId(String memberId);
}
