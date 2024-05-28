package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.domain.Comment;
import com.capstone.safeGuard.domain.Emergency;
import com.capstone.safeGuard.dto.request.emergency.CommentRequestDTO;
import com.capstone.safeGuard.dto.request.emergency.EmergencyIdDTO;
import com.capstone.safeGuard.dto.request.emergency.EmergencyRequestDTO;
import com.capstone.safeGuard.dto.request.emergency.MemberIdDTO;
import com.capstone.safeGuard.dto.response.CommentResponseDTO;
import com.capstone.safeGuard.dto.response.EmergencyResponseDTO;
import com.capstone.safeGuard.dto.response.FindNotificationResponse;
import com.capstone.safeGuard.service.EmergencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.format.DateTimeFormatter;
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

    @Transactional
    @PostMapping("/emergency")
    public ResponseEntity<Map<String, String>> emergencyCall(@RequestBody EmergencyRequestDTO emergencyRequestDto) {
        Map<String, String> result = new HashMap<>();

        // 1. 반경 [] km내의 member들만 리스트업
        ArrayList<String> neighborMemberList
                = emergencyService.getNeighborMembers(emergencyRequestDto, DISTANCE);

        // 2. 반경 []km 내의 member 들에게 알림을 보냄
        if (!sendEmergencyToMembers(neighborMemberList, emergencyRequestDto)) {
            return addErrorStatus(result);
        }

        return addOkStatus(result);
    }

    @Transactional
    public boolean sendEmergencyToMembers(ArrayList<String> neighborMemberList, EmergencyRequestDTO dto) {
        for (String memberId : neighborMemberList) {
            // 3. 알림을 전송 및 저장
            Emergency emergency = emergencyService.saveEmergency(memberId, dto);
            if (emergency == null) {
                return false;
            }

            if (!emergencyService.sendNotificationTo(memberId, emergency)) {
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
    public ResponseEntity<Map<String, FindNotificationResponse>> showSentEmergency(@RequestBody MemberIdDTO dto) {
        List<Emergency> sentEmergencyList = emergencyService.getSentEmergency(dto.getMemberId());

        HashMap<String, FindNotificationResponse> result = addEmergencyList(sentEmergencyList);
        if (result == null) {
            return ResponseEntity.status(400).body(result);
        }

        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/received-emergency")
    public ResponseEntity<Map<String, FindNotificationResponse>> showReceivedEmergency(@RequestBody MemberIdDTO dto) {
        List<Emergency> sentEmergencyList = emergencyService.getReceivedEmergency(dto.getMemberId());

        HashMap<String, FindNotificationResponse> result = addEmergencyList(sentEmergencyList);
        if (result == null) {
            return ResponseEntity.status(400).body(result);
        }

        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/write-comment")
    public ResponseEntity<Map<String, String>> writeComment(@RequestBody CommentRequestDTO commentRequestDTO) {
        HashMap<String, String> result = new HashMap<>();

        if (!emergencyService.writeComment(commentRequestDTO)) {
            return addErrorStatus(result);
        }

        return addOkStatus(result);
    }

    @Transactional
    @PostMapping("/emergency-detail")
    public ResponseEntity<Map<String, EmergencyResponseDTO>> emergencyDetail(@RequestBody EmergencyIdDTO dto) {
        HashMap<String, EmergencyResponseDTO> result = new HashMap<>();

        Emergency emergency = emergencyService.getEmergencyDetail(dto.getEmergencyId());
        if (emergency == null) {
            return ResponseEntity.status(400).body(result);
        }

        List<Comment> commentList = emergencyService.getCommentOfEmergency(dto.getEmergencyId());
        if(commentList == null) {
            result.put("Emergecny", EmergencyResponseDTO.builder()
                    .emergencyTitle(emergency.getTitle())
                    .emergencyContent(emergency.getContent())
                    .emergencyDate(emergency.getCreatedAt().toString())
                    .build()
            );
            return ResponseEntity.ok().body(result);
        }

        List<CommentResponseDTO> dtoList = new ArrayList<>();
        for (Comment comment : commentList) {
            dtoList.add(
                    CommentResponseDTO.builder()
                            .commentId(comment.getCommentId())
                            .commentator(comment.getCommentator().getMemberId())
                            .commentDate(comment.getCreatedAt().toString())
                            .content(comment.getComment())
                            .build()
            );
        }

        result.put("Emergecny", EmergencyResponseDTO.builder()
                .emergencyTitle(emergency.getTitle())
                .emergencyContent(emergency.getContent())
                .emergencyDate(emergency.getCreatedAt().toString())
                .emergencyCommentList(dtoList)
                .build()
        );

        return ResponseEntity.ok().body(result);
    }

    private static HashMap<String, FindNotificationResponse> addEmergencyList(List<Emergency> sentEmergencyList) {
        HashMap<String, FindNotificationResponse> result = new HashMap<>();

        if (sentEmergencyList == null) {
            return null;
        }

        for (Emergency emergency : sentEmergencyList) {
            String format = emergency.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));

            result.put(emergency.getEmergencyId() + "",
                    FindNotificationResponse.builder()
                            .title("도움 요청")
                            .content(emergency.getContent())
                            .child(emergency.getChild().getChildName())
                            .date(format)
                            .build()
            );
        }

        return result;
    }
}
