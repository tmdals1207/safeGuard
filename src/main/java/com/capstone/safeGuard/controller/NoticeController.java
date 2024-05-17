package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.NoticeLevel;
import com.capstone.safeGuard.repository.NoticeRepository;
import com.capstone.safeGuard.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NoticeController {
    private final MemberService memberService;
    private final NoticeRepository noticeRepository;

    public ResponseEntity<Map<String, String>> sendNotice(String childName) {
        Map<String, String> result = new HashMap<>();
        Child foundChild = memberService.findChildByChildName(childName);

        // TODO foundChild의 위치에 따른 sendNotice


        return ResponseEntity.ok(result);
    }

    private boolean sendNoticeToMember(String receiverId, String childName, NoticeLevel noticeLevel) {
        // TODO fcm을 이용한 sendNotice

        // notice 저장



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

