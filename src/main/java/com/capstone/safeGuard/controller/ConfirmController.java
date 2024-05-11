package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Helping;
import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.dto.request.confirm.SendConfirmRequest;
import com.capstone.safeGuard.repository.ChildRepository;
import com.capstone.safeGuard.repository.MemberRepository;
import com.capstone.safeGuard.service.ConfirmService;
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
@Slf4j
@RequiredArgsConstructor
public class ConfirmController {
    private final MemberRepository memberRepository;
    private final ChildRepository childRepository;
    private final ConfirmService confirmService;

    @PostMapping("/send-confirm-request")
    public ResponseEntity<Map<String, String>> sendConfirmRequest(@RequestBody SendConfirmRequest dto){
        Map<String, String> result = new HashMap<>();

        Optional<Member> foundSender = memberRepository.findById(dto.getSenderId());
        Child foundChild = childRepository.findBychildName(dto.getChildName());

        if(foundSender.isEmpty() || foundChild == null ){
            log.info("없는 멤버");
            return addErrorStatus(result);
        }

        if (! isHelper(foundSender.get(), foundChild)){
            log.info("관계가 잘못됨");
            return addErrorStatus(result);
        }

        if(! confirmService.sendConfirm(foundSender.get(), foundChild)){
            return addErrorStatus(result);
        }

        return addOkStatus(result);
    }

    public boolean isHelper(Member member, Child child){
        List<Helping> memberHelpingList = member.getHelpingList();
        List<Helping> childHelpingList = child.getHelpingList();

        for (Helping helping : memberHelpingList) {
            if(childHelpingList.contains(helping)){
                return true;
            }
        }
        return false;
    }

    private static ResponseEntity<Map<String, String>> addErrorStatus(Map<String, String> result) {
        result.put("status", "400");
        return ResponseEntity.status(400).body(result);
    }

    private static ResponseEntity<Map<String, String>> addOkStatus(Map<String, String> result) {
        result.put("status", "200");
        return ResponseEntity.ok().body(result);
    }


}
