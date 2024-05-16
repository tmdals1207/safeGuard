package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.domain.Parenting;
import com.capstone.safeGuard.repository.ChildRepository;
import com.capstone.safeGuard.repository.FcmTokenRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfirmService {
    private final ChildRepository childRepository;
//    private final FirebaseMessaging firebaseMessaging;
    private final FcmTokenRepository fcmTokenRepository;

    public boolean sendConfirm(Member sender, Child child) {
        // 1. child의 parent 리스트 찾기
        List<Parenting> parentingList = child.getParentingList();



        if(parentingList.isEmpty()){
            return false;
        }

        // 2. child의 모든 parent에게 알림 전송
//        for (Parenting parenting : parentingList) {
//            Member parent = parenting.getParent();
//
//            // parent_token를 찾기
//            String parentMemberId= parent.getMemberId();
//            List<String> parentToken = fcmTokenRepository.findAllTokenByMemberId(parentMemberId);
//
//            try {
//                for (String tokenItem : parentToken) {
//                    Message msg = Message.builder()
//                            .setToken(tokenItem)
//                            .setNotification(new Notification("Confirm", "Children arrived"))
//                            .build();
//                    firebaseMessaging.send(msg);
//                }
//            } catch (FirebaseMessagingException e) {
//                throw new RuntimeException(e);
//            }
//
//        }

        return true;
    }

}