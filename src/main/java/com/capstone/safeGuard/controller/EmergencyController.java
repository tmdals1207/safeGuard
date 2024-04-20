package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.dto.request.EmergencyDto;
import com.capstone.safeGuard.service.EmergencyService;
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
    static public final int LENGTH = 1;
    private final EmergencyService emergencyService;

    @PostMapping("/emergency")
    public ResponseEntity emergencyCall(@RequestBody EmergencyDto emergencyDto) {
        // 1. 다른 member들의 위치를 요청 -> 응답으로 받은 위치를 저장
        Map<String, int[]> memberIdCoordinateHashMap = getAllMemberCoordinate();

        // 2. emergencyDto의 위도, 경도와 비교 -> 반경 [] km내의 member들만 리스트업
        ArrayList<String> neighborMemberList = emergencyService.compareCoordinate(emergencyDto, memberIdCoordinateHashMap, LENGTH);

        // 3. 반경 []km 내의 member 들에게 알림을 보냄
        sendEmergencyToMembers(neighborMemberList);

        // 4. emergency table에 저장
        emergencyService.saveEmergency(emergencyDto);

        return ResponseEntity.ok().build();
    }

    public HashMap<String, int[]> getAllMemberCoordinate(){
        int[] memberCoordinate = new int[2];
        HashMap<String, int[]> memberIdCoordinateMap = new HashMap<>();

        // TODO 다른 member들의 위치를 요청 -> 응답으로 받은 위치를 저장

        return memberIdCoordinateMap;
    }

    public void sendEmergencyToMembers(ArrayList<String> neighborMemberList){
        // TODO 반경 []km 내의 member 들에게 알림을 보냄
    }

}
