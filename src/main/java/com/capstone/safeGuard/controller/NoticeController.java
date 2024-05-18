package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.NoticeLevel;
import com.capstone.safeGuard.domain.Parenting;
import com.capstone.safeGuard.repository.NoticeRepository;
import com.capstone.safeGuard.service.MemberService;
import com.capstone.safeGuard.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NoticeController {
    private final MemberService memberService;
    private final NoticeService noticeService;

    public String sendNotice(String childName) {
        Child foundChild = memberService.findChildByChildName(childName);

        // TODO foundChild의 위치에 따른 sendNotice


        List<Parenting> childParentingList = foundChild.getParentingList();
        // 1. if 안전 -> 중립
        for (Parenting parenting : childParentingList) {
            if(! sendNoticeToMember(parenting.getParent().getMemberId(), foundChild, NoticeLevel.INFO)){
                return "에러 : 전송 실패";
            }
        }
//        return "전송 완료";

        // 2. if 중립 -> 위험
        for (Parenting parenting : childParentingList) {
            if(! sendNoticeToMember(parenting.getParent().getMemberId(), foundChild, NoticeLevel.WARN)){
                return "에러 : 전송 실패";
            }
        }
//        return "전송 완료";

        return null;
    }

    public boolean sendNoticeToMember(String receiverId, Child child, NoticeLevel noticeLevel) {
        // TODO fcm을 이용한 sendNotice

        // notice 저장
        noticeService.saveNotice(child, noticeLevel);

        return true;
    }

    private static ResponseEntity<Map<String, String>> addOkStatus(Map<String, String> result) {
        result.put("status", "200");
        return ResponseEntity.ok().body(result);
    }

    private static ResponseEntity<Map<String, String>> addErrorStatus(Map<String, String> result) {
        result.put("status", "400");
        return ResponseEntity.status(400).body(result);
    }
}

