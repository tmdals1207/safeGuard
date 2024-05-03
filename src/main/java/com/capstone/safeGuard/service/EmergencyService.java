package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Comment;
import com.capstone.safeGuard.domain.Emergency;
import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.dto.request.emergency.CommentRequestDTO;
import com.capstone.safeGuard.dto.request.emergency.EmergencyRequestDTO;
import com.capstone.safeGuard.dto.request.emergency.FcmMessageDTO;
import com.capstone.safeGuard.repository.ChildRepository;
import com.capstone.safeGuard.repository.CommentRepository;
import com.capstone.safeGuard.repository.EmergencyRepository;
import com.capstone.safeGuard.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class EmergencyService {
    private final EmergencyRepository emergencyRepository;
    private final MemberRepository memberRepository;
    private final ChildRepository childRepository;
    private final MemberService memberService;
    private final CommentRepository commentRepository;

    public ArrayList<String> getNeighborMembers(EmergencyRequestDTO dto, int distance){
        ArrayList<String> memberIdList = new ArrayList<>();
        ArrayList<Member> allMember = memberService.findAllMember();

        for (Member member : allMember) {
            if (isNeighbor(dto.getLatitude(), dto.getLongitude(), member.getLatitude(), member.getLongitude(), distance)){
                memberIdList.add(member.getMemberId());
            }
        }

        return memberIdList;
    }

    private boolean isNeighbor(float latitude, float longitude, float memberLatitude, float memberLongitude, int length) {
        double x_distance = latitude - memberLatitude;
        double y_distance = longitude - memberLongitude;

        // 좌표 -> km
        double distance = convertCoordinateToKm(x_distance, y_distance);

        return distance <= (length);
    }

    private double convertCoordinateToKm(double x_coordinate, double y_coordinate) {
        // 위도 35~38도(한국) 기준으로
        // 대략 위도는 1도당 111km, 경도는 1도당 89km
        double x_km = 111 * x_coordinate;
        double y_km = 89 * y_coordinate;

        return Math.sqrt( (x_km * x_km) + (y_km * y_km) );
    }

    public boolean saveEmergency(String receiverId, EmergencyRequestDTO emergencyRequestDto) {
        // Emergency table에 저장
        Member member = memberRepository.findById(emergencyRequestDto.getSenderId()).orElseThrow(NoSuchElementException::new);
        Child child = childRepository.findBychildName(emergencyRequestDto.getChildName());
        String message = makeMessage(receiverId, emergencyRequestDto);
        if (message == null) {
            return false;
        }
        emergencyRepository.save(emergencyRequestDto.dtoToDomain(member, child, message));
        return true;
    }

    public boolean sendNotificationTo(String receiverId, EmergencyRequestDTO dto) {
        // TODO member에게 알림 보내기 테스트
        String message = makeMessage(receiverId, dto);
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            headers.set("Authorization", "Bearer " + getAccessToken());
        } catch (IOException e) {
            return false;
        }

        HttpEntity entity = new HttpEntity<>(message, headers);
        String API_URL = "https://fcm.googleapis.com/v1/projects/safeguard-2f704/messages:send";

        ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

        return response.getStatusCode() == HttpStatus.OK;
    }

    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/safeguard-2f704-firebase-adminsdk-pmiwx-0bced8bb31.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    private String makeMessage(String receiverId, EmergencyRequestDTO dto) {
        // 1. receiverId 이용해서 받을 member의 토큰 값 가져오기
        Member foundMember = memberRepository.findById(receiverId).orElseThrow(NoSuchElementException::new);
        String token = foundMember.getFcmToken();

        // 2. dto의 childId를 이용해서 보내는 child의 정보를 가져오기
        Child foundChild = childRepository.findBychildName(dto.getChildName());
        if (foundChild == null) {
            throw new NoSuchElementException();
        }
        // TODO Child에 추가 정보를 저장해 보여주는 정보 추가 논의
        String body = "아이 이름 : " + foundChild.getChildName();

        ObjectMapper om = new ObjectMapper();
        FcmMessageDTO fcmMessageDto = FcmMessageDTO.builder()
                .message(FcmMessageDTO.Message.builder()
                        .token(token)
                        .notification(FcmMessageDTO.Notification.builder()
                                .title(dto.getTitle())
                                .body(body)
                                .build()
                        ).build()).validateOnly(false).build();

        try {
            return om.writeValueAsString(fcmMessageDto);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public List<Emergency> getSentEmergency(String memberId) {
        Member member = memberRepository.findById(memberId).get();
        List<Emergency> foundEmergency = emergencyRepository.findAllBySenderId(member);
        if (foundEmergency.isEmpty()){
            return null;
        }

        return foundEmergency;
    }

    // TODO 받은 emergency 및 comment 조회 -> 어케하냐
    public List<Emergency> getReceivedEmergency(String memberId) {

        return null;
    }

    public boolean writeEmergency(CommentRequestDTO commentRequestDTO) {
        Comment comment = Comment.builder()
                .commentator(memberRepository.findById(commentRequestDTO.getCommentatorId()).get())
                .emergency(emergencyRepository.findById(Long.valueOf(commentRequestDTO.getEmergencyId())).get())
                .comment(commentRequestDTO.getCommentContent())
                .build();

        commentRepository.save(comment);

        return true;
    }

    // TODO emergency 디테일 추가
}
