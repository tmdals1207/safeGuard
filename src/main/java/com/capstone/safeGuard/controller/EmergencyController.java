package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.dto.request.emergency.EmergencyDTO;
import com.capstone.safeGuard.service.EmergencyService;
import com.capstone.safeGuard.service.FcmService;
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
    /**
     * 몇 km 이내의 사람들에게 알림을 보낼 것인지
     */
    static public final int DISTANCE = 1;
    private final EmergencyService emergencyService;
    private final MemberService memberService;
    private final FcmService fcmService;

    @PostMapping("/emergency")
    public ResponseEntity<Map<String, String>> emergencyCall(@RequestBody EmergencyDTO emergencyDto) {
        Map<String, String> result = new HashMap<>();

        // 1. 다른 member들의 위치를 요청 -> 응답으로 받은 위치를 저장
        // key = ID, value = x, y
        Map<String, int[]> memberIdCoordinateHashMap = getAllMemberCoordinate();

        if(memberIdCoordinateHashMap == null) {
            result.put("status", "400");
            return ResponseEntity.ok().body(result);
        }

        // 2. emergencyDto의 위도, 경도와 비교 -> 반경 [] km내의 member들만 리스트업
        ArrayList<String> neighborMemberList
                = emergencyService.compareCoordinate(emergencyDto, memberIdCoordinateHashMap, DISTANCE);

        // 3. 반경 []km 내의 member 들에게 알림을 보냄
        if (! sendEmergencyToMembers(neighborMemberList)){
            result.put("status", "400");
            return ResponseEntity.ok().body(result);
        }

        // 4. emergency table에 저장
        emergencyService.saveEmergency(emergencyDto);

        result.put("status", "200");
        return ResponseEntity.ok().body(result);
    }

    public HashMap<String, int[]> getAllMemberCoordinate(){
        HashMap<String, int[]> memberIdCoordinateMap = new HashMap<>();

        ArrayList<Member> allMember = memberService.findAllMember();
        for (Member member : allMember) {
            // member 위치 요청해서 받은 위치를 넣기
            memberIdCoordinateMap.put(member.getMemberId(), fcmService.requestLocation(member));
        }

        return memberIdCoordinateMap;
    }

    public boolean sendEmergencyToMembers(ArrayList<String> neighborMemberList){
        for (String memberId : neighborMemberList) {
            fcmService.sendNotification(memberId);
        }
        return false;
    }

}
