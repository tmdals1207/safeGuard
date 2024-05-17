package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.dto.request.confirm.SendConfirmRequest;
import com.capstone.safeGuard.repository.ConfirmRepository;
import com.capstone.safeGuard.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ConfirmController {
    private final MemberService memberService;
    private final ConfirmRepository confirmRepository;

    @PostMapping("/send-confirm")
    public ResponseEntity<Map<String, String>> sendConfirm(@RequestBody SendConfirmRequest dto) {
        Map<String, String> result = new HashMap<>();

        // 1. chidname 확인
        Child foundChild = memberService.findChildByChildName(dto.getChildName());
        if(foundChild == null){
            return addErrorStatus(result);
        }

        // 2. 해당 child의 member에게 전송
        Member foundMember = memberService.findParentByChild(foundChild);
        if(foundMember == null){
            return addErrorStatus(result);
        }

        if(! sendConfirmToMember(foundMember.getMemberId(), foundChild.getChildName(), dto.getSenderId())){
            return addErrorStatus(result);
        }

        return addOkStatus(result);
    }

    private boolean sendConfirmToMember(String receiverId, String childName, String senderId) {
        // TODO fcm을 이용한 sendConfirm

        // confirm 저장
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
