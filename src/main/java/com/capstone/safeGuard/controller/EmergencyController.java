package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.domain.Comment;
import com.capstone.safeGuard.domain.Emergency;
import com.capstone.safeGuard.dto.request.emergency.CommentRequestDTO;
import com.capstone.safeGuard.dto.request.emergency.EmergencyRequestDTO;
import com.capstone.safeGuard.service.EmergencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class EmergencyController {

    // 몇 km 이내의 사람들에게 알림을 보낼 것인지
    static public final int DISTANCE = 1;

    private final EmergencyService emergencyService;

    @PostMapping("/emergency")
    public ResponseEntity<Map<String, String>> emergencyCall(@RequestBody EmergencyRequestDTO emergencyRequestDto) {
        Map<String, String> result = new HashMap<>();

        // 1. 반경 [] km내의 member들만 리스트업
        ArrayList<String> neighborMemberList
                = emergencyService.getNeighborMembers(emergencyRequestDto, DISTANCE);

        // 2. 반경 []km 내의 member 들에게 알림을 보냄
        if (! sendEmergencyToMembers(neighborMemberList, emergencyRequestDto)){
            return addErrorStatus(result);
        }

        return addOkStatus(result);
    }

    public boolean sendEmergencyToMembers(ArrayList<String> neighborMemberList, EmergencyRequestDTO dto){
        for (String memberId : neighborMemberList) {
            // 3. 알림을 전송 및 저장
            if(! emergencyService.sendNotificationTo(memberId, dto)){
                return false;
            }
        }
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

    @PostMapping("/sent-emergency")
    public ResponseEntity<Map<String, String>> showSentEmergency(@RequestBody String memberId){
        List<Emergency> sentEmergencyList = emergencyService.getSentEmergency(memberId);

        HashMap<String, String> result = addEmergencyList(sentEmergencyList);
        if (result == null) {
            return addErrorStatus(result);
        }

        return addOkStatus(result);
    }

    @PostMapping("/recieved-emergency")
    public ResponseEntity<Map<String, String>> showReceivedEmergency(@RequestBody String memberId){
        List<Emergency> sentEmergencyList = emergencyService.getReceivedEmergency(memberId);

        HashMap<String, String> result = addEmergencyList(sentEmergencyList);
        if (result == null) {
            return addErrorStatus(result);
        }

        return addOkStatus(result);
    }

    @PostMapping("/write-comment")
    public ResponseEntity<Map<String, String>> writeComment(@RequestBody CommentRequestDTO commentRequestDTO){
        HashMap<String, String> result = new HashMap<>();

        if (! emergencyService.writeEmergency(commentRequestDTO)){
            return addErrorStatus(result);
        }

        return addOkStatus(result);
    }

    @PostMapping("/emergency-detail")
    public ResponseEntity<Map<String, String>> emergencyDetail(@RequestBody String emergencyId){
        HashMap<String, String> result = new HashMap<>();

        Emergency emergency  = emergencyService.getEmergencyDetail(emergencyId);
        if(emergency == null){
            return addErrorStatus(result);
        }

        result.put("Title", emergency.getTitle());
        result.put("Content", emergency.getContent());

        List<Comment> commentList = emergency.getCommentList();
        for (Comment comment : commentList) {
            result.put("writer", comment.getCommentator().getName());
            result.put("comment", comment.getComment());
            result.put("written at", comment.getCreatedAt().toString());
        }

        return addOkStatus(result);
    }

    private static HashMap<String, String> addEmergencyList(List<Emergency> sentEmergencyList) {
        HashMap<String, String> result = new HashMap<>();

        if (sentEmergencyList == null){
            return null;
        }
        for (Emergency emergency : sentEmergencyList) {
            result.put(emergency.getEmergencyId() + "", emergency.getTitle());
        }

        return result;
    }
}
