package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.dto.request.emergency.EmergencyRequestDTO;
import com.capstone.safeGuard.service.EmergencyService;
import com.capstone.safeGuard.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class EmergencyController {

    // 몇 km 이내의 사람들에게 알림을 보낼 것인지
    static public final int DISTANCE = 1;

    private final EmergencyService emergencyService;
    private final MemberService memberService;

    @PostMapping("/emergency")
    public ResponseEntity<Map<String, String>> emergencyCall(@RequestBody EmergencyRequestDTO emergencyRequestDto) {
        Map<String, String> result = new HashMap<>();

        // 1. 반경 [] km내의 member들만 리스트업
        ArrayList<String> neighborMemberList
                = emergencyService.getNeighborMembers(emergencyRequestDto, DISTANCE);

        // 2. 반경 []km 내의 member 들에게 알림을 보냄
        if (! sendEmergencyToMembers(neighborMemberList, emergencyRequestDto)){
            result.put("status", "400");
            return ResponseEntity.ok().body(result);
        }

        // 3. emergency table에 저장
        emergencyService.saveEmergency(emergencyRequestDto);

        result.put("status", "200");
        return ResponseEntity.ok().body(result);
    }

    public boolean sendEmergencyToMembers(ArrayList<String> neighborMemberList, EmergencyRequestDTO dto){
        for (String memberId : neighborMemberList) {
            if(! emergencyService.sendNotificationTo(memberId, dto)){
                return false;
            }
        }
        return true;
    }
}
