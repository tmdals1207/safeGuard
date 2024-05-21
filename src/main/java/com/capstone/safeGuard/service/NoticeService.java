package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Notice;
import com.capstone.safeGuard.domain.NoticeLevel;
import com.capstone.safeGuard.dto.request.notification.FCMNotificationDTO;
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
    private final FCMService fcmService;

    @Transactional
    public Notice createNotice(String receiverId, String childName, NoticeLevel noticeLevel) {
        Notice notice = new Notice();
        notice.setTitle(noticeLevel.name());
        notice.setContent("아이 이름 : " + childName);
        notice.setReceiverId(receiverId);

        if(! memberRepository.existsByMemberId(receiverId)) {
            log.info("createNotice memberId not exist!!");
            return null;
        }
        notice.setNoticeLevel(noticeLevel);

        Child child = childRepository.findByChildName(childName);

        if (child == null) {
            log.info("createNotice childName not exist!!");
            return null;
        }
        notice.setChild(child);
        notice.setCreatedAt(LocalDateTime.now());

        noticeRepository.save(notice);

        return notice;
    }

    public boolean sendNotificationTo(Notice notice){
        FCMNotificationDTO message = makeMessage(notice);
        return fcmService.SendNotificationByToken(message) != null;
    }

    private FCMNotificationDTO makeMessage(Notice notice) {
        return FCMNotificationDTO.builder()
                .title(notice.getTitle())
                .body(notice.getContent())
                .receiverId(notice.getReceiverId())
                .build();
    }
}
