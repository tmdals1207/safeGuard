package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.*;
import com.capstone.safeGuard.dto.request.notification.FCMNotificationDTO;
import com.capstone.safeGuard.repository.ConfirmRepository;
import com.capstone.safeGuard.repository.HelpingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmService {
    private final ConfirmRepository confirmRepository;
    private final FCMService fcmService;
    private final MemberService memberService;
    private final HelpingRepository helpingRepository;

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
        confirm.setHelpingId(helping);
        confirmRepository.save(confirm);

        return confirm;
    }

    public boolean sendNotificationTo(String receiverId, Confirm confirm){
        FCMNotificationDTO message = makeMessage(receiverId, confirm);
        return fcmService.SendNotificationByToken(message) != null;
    }

    private FCMNotificationDTO makeMessage(String receiverId, Confirm confirm) {

        String tmpTitle = "아이의 도착이 확인되지 않습니다.";
        if(confirm.getConfirmType().equals(ConfirmType.ARRIVED)) {
            tmpTitle = "아이가 도착 완료했습니다.";
        } else if(confirm.getConfirmType().equals(ConfirmType.DEPART)) {
            tmpTitle = "아이가 출발했습니다.";
        }

        return FCMNotificationDTO.builder()
                .title(tmpTitle)
                .body(confirm.getContent())
                .receiverId(receiverId)
                .build();
    }

    @Transactional
    public List<Confirm> findReceivedConfirmByMember(String id) {
        Member foundMember = memberService.findMemberById(id);
        if(foundMember == null){
            log.info("No member found for id : " + id);
            return null;
        }

        List<Parenting> parentingList = foundMember.getParentingList();
        if(parentingList == null || parentingList.isEmpty()){
            log.info("No parenting found for id : " + id);
            return null;
        }

        ArrayList<Confirm> confirmList = new ArrayList<>();
        for (Parenting parenting : parentingList) {
            confirmList.addAll(confirmRepository
                    .findAllByChild( parenting.getChild() ));
        }

        return confirmList;
    }

    public ArrayList<Confirm> findSentConfirmByMember(String id) {
        Member foundMember = memberService.findMemberById(id);
        if(foundMember == null){
            log.info("No member found for id : " + id);
            return null;
        }

        List<Helping> helpingList = helpingRepository.findAllByHelper(foundMember);
        if(helpingList == null || helpingList.isEmpty()){
            log.info("No helping found for id : " + id);
            return null;
        }

        ArrayList<Confirm> confirmList = new ArrayList<>();
        for (Helping helping : helpingList) {
            confirmList.addAll(confirmRepository.findAllByHelpingId(helping));
        }

        return confirmList;
    }
}
