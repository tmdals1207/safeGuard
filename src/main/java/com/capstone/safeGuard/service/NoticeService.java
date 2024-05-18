package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Notice;
import com.capstone.safeGuard.domain.NoticeLevel;
import com.capstone.safeGuard.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoticeService {
    private final NoticeRepository noticeRepository;

    @Transactional
    public void saveNotice(Child child, NoticeLevel noticeLevel) {
        Notice notice = new Notice();
        notice.setChild(child);
        notice.setNoticeLevel(noticeLevel);
        notice.setTitle("");
        notice.setContent("");
        notice.setCreatedAt(LocalDateTime.now());
        noticeRepository.save(notice);
    }
}
