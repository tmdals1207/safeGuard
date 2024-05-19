package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Notice;
import com.capstone.safeGuard.domain.NoticeLevel;
import com.capstone.safeGuard.repository.ChildRepository;
import com.capstone.safeGuard.repository.FcmTokenRepository;
import com.capstone.safeGuard.repository.NoticeRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class FcmService {

    private final ChildRepository childRepository;
    private final FirebaseMessaging firebaseMessaging;
    private final FcmTokenRepository fcmTokenRepository;
    private final NoticeRepository noticeRepository;

    void sendFcm(List<String> token) {
        try {
            for (String tokenItem : token) {
                Message msg = Message.builder()
                        .setToken(tokenItem)
                        .setNotification(new Notification("warning", "Children are not in the safe zone"))
                        .build();
                firebaseMessaging.send(msg);
            }
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendNoticeReport(String childUserName) {
        List<String> parentUserNames = childRepository.findAllMemberByChildName(childUserName);
        sendFcm(findFcmToken(parentUserNames));
    }

    private List<String> findFcmToken(List<String> member) {
        List<String> token = new ArrayList<>();
        for (String mem : member) {
            List<String> memToken = fcmTokenRepository.findAllTokenByMemberId(mem);
            token.addAll(memToken);
        }
        return token;
    }
    private Notice createNotice( String childName, String memberId){
        Child child= childRepository.findByChildName(childName);
        Notice notice= new Notice();
        String title="";
        NoticeLevel noticeLevel =NoticeLevel.INFO;
        notice.setTitle(title);
        notice.setCreatedAt(LocalDateTime.now());
        notice.setNoticeLevel(noticeLevel);
        notice.setChild(child);
        noticeRepository.save(notice);
        return notice;
    }

}
