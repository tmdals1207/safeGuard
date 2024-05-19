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

    public boolean sendNotificationTo(Notice notice){
        // TODO fcm을 이용한 sendNotice



        return true;
    }


    @Transactional
    public Notice createNotice(String receiverId, String childName, NoticeLevel noticeLevel, String message) {
        Notice notice = new Notice();
        // TODO 제목, 내용 적기
        notice.setTitle("Title");
        notice.setContent("Content");
        notice.setReceiverId(receiverId);

        if(! memberRepository.existsByMemberId(receiverId)) {
            return null;
        }
        notice.setNoticeLevel(noticeLevel);

        Child child = childRepository.findByChildName(childName);

        if (child == null) {
            return null;
        }
        notice.setChild(child);
        notice.setCreatedAt(LocalDateTime.now());

        noticeRepository.save(notice);

        return notice;
    }
}
