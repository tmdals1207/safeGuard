package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Confirm;
import com.capstone.safeGuard.domain.Helping;
import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.dto.request.confirm.SendConfirmRequest;
import com.capstone.safeGuard.repository.MemberRepository;
import com.capstone.safeGuard.service.ConfirmService;
import com.capstone.safeGuard.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ConfirmController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final ConfirmService confirmService;

    @PostMapping("/send-confirm")
    public ResponseEntity<Map<String, String>> sendConfirm(@RequestBody SendConfirmRequest dto) {
        Map<String, String> result = new HashMap<>();

        // 1. chidname 확인
        Child foundChild = memberService.findChildByChildName(dto.getChildName());
        if(foundChild == null){
            return addErrorStatus(result);
        }

        Optional<Member> foundSender = memberRepository.findById(dto.getSenderId());
        if(foundSender.isEmpty()){
            return addErrorStatus(result);
        }

        // 2. 해당 child의 member에게 전송
        Member foundMember = memberService.findParentByChild(foundChild);
        if(foundMember == null){
            return addErrorStatus(result);
        }

        // helper가 helpinglist에 존재하는지 확인
        List<Helping> childHelpingList = foundChild.getHelpingList();

        boolean isSent = false;
        for (Helping helping : childHelpingList) {
            if(helping.getHelper().equals(foundSender.get())){
                // helper가 존재하면 confirm 전송
                isSent = sendConfirmToMember(foundMember.getMemberId(), foundChild, helping, dto.getConfirmType());
            }
        }
        if(! isSent){
            return addErrorStatus(result);
        }

        return addOkStatus(result);
    }

    private boolean sendConfirmToMember(String receiverId, Child child, Helping helping, String confirmType) {
        Confirm confirm = confirmService.saveConfirm(child, helping, confirmType);
        if(confirm == null){
            return false;
        }

        // TODO fcm을 이용한 sendConfirm
        return confirmService.sendNotificationTo(receiverId, confirm);
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
