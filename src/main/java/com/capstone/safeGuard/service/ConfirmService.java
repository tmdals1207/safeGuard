package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Confirm;
import com.capstone.safeGuard.domain.ConfirmType;
import com.capstone.safeGuard.domain.Helping;
import com.capstone.safeGuard.dto.request.notification.FCMNotificationDTO;
import com.capstone.safeGuard.repository.ConfirmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmService {
    private final ConfirmRepository confirmRepository;
    private final FCMService fcmService;

    @Transactional
    public Confirm saveConfirm(Child child, Helping helping, String confirmType) {
        Confirm confirm = new Confirm();
        if( confirmType.equals("ARRIVED") ){
            confirm.setConfirmType(ConfirmType.ARRIVED);
        } else if( confirmType.equals("DEPART") ){
            confirm.setConfirmType(ConfirmType.DEPART);
        } else {
            confirm.setConfirmType(ConfirmType.UNCONFIRMED);
        }

        confirm.setChild(child);
        confirm.setTitle(confirmType);
        confirm.setContent("아이 이름 : " + child.getChildName());
        confirm.setCreatedAt(LocalDateTime.now());
        confirm.setHelping_id(helping);
        confirmRepository.save(confirm);

        return confirm;
    }

    public boolean sendNotificationTo(String receiverId, Confirm confirm){
        FCMNotificationDTO message = makeMessage(receiverId, confirm);
        return fcmService.SendNotificationByToken(message) != null;
    }

    private FCMNotificationDTO makeMessage(String receiverId, Confirm confirm) {
        return FCMNotificationDTO.builder()
                .title(confirm.getTitle())
                .body(confirm.getContent())
                .receiverId(receiverId)
                .build();
    }
}
