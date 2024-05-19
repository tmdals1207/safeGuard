package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Notice;
import com.capstone.safeGuard.domain.NoticeLevel;
import com.capstone.safeGuard.repository.ChildRepository;
import com.capstone.safeGuard.repository.MemberRepository;
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
    private final MemberRepository memberRepository;
    private final ChildRepository childRepository;

    @Transactional
    public void saveNotice(Child child, NoticeLevel noticeLevel) {
        Notice notice = new Notice();
        notice.setChild(child);
        notice.setNoticeLevel(noticeLevel);
        // TODO 제목, 내용 적기
        notice.setTitle("");
        notice.setContent("");
        notice.setCreatedAt(LocalDateTime.now());
        noticeRepository.save(notice);
    }

    public Boolean createNotice(String receiverId, String childName, NoticeLevel noticeLevel, String message) {
        Notice notice = new Notice();
        // TODO 제목, 내용 적기
        notice.setTitle("Title");
        notice.setContent("Content");
        notice.setReceiverId(receiverId);

        if(! memberRepository.existsByMemberId(receiverId)) {
            return false;
        }
        notice.setNoticeLevel(noticeLevel);

        Child child = childRepository.findByChildName(childName);

        if (child == null) {
            return false;
        }
        notice.setChild(child);
        notice.setCreatedAt(LocalDateTime.now());

        noticeRepository.save(notice);

        return true;

    }
}
