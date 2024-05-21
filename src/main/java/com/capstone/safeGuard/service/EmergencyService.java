package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.*;
import com.capstone.safeGuard.dto.request.emergency.CommentRequestDTO;
import com.capstone.safeGuard.dto.request.emergency.EmergencyRequestDTO;
import com.capstone.safeGuard.dto.request.notification.FCMNotificationDTO;
import com.capstone.safeGuard.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmergencyService {
    private final EmergencyRepository emergencyRepository;
    private final MemberRepository memberRepository;
    private final ChildRepository childRepository;
    private final MemberService memberService;
    private final CommentRepository commentRepository;
    private final EmergencyReceiverRepository emergencyReceiverRepository;
    private final FCMService fcmService;

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

    private boolean isNeighbor(double latitude, double longitude, double memberLatitude, double memberLongitude, int length) {
        double latitudeDistance = latitude - memberLatitude;
        double longitudeDistance = longitude - memberLongitude;

        // 좌표 -> km
        double distance = convertCoordinateToKm(latitudeDistance, longitudeDistance);

        return distance <= (length);
    }

    private double convertCoordinateToKm(double latitudeDistance, double longitudeDistance) {
        // 위도 35~38도(한국) 기준으로
        // 대략 위도는 1도당 111km, 경도는 1도당 89km
        double latitudeKm = 111 * latitudeDistance;
        double longitudeKm = 89 * longitudeDistance;

        return Math.sqrt( (latitudeKm * latitudeKm) + (longitudeKm * longitudeKm) );
    }

    @Transactional
    public Emergency saveEmergency(String receiverId, EmergencyRequestDTO dto) {
        // Emergency table에 저장
        Member member = memberRepository.findById(dto.getSenderId()).orElseThrow(NoSuchElementException::new);
        Child child = childRepository.findBychildName(dto.getChildName());
        String content = "아이 이름 : " + dto.getChildName();

        Emergency emergency = dto.dtoToDomain(member, child, content);
        emergencyRepository.save(emergency);

        EmergencyReceiver emergencyReceiver = EmergencyReceiver.builder()
                .emergency(emergency)
                .emergencyReceiverId(receiverId)
                .build();
        emergencyReceiverRepository.save(emergencyReceiver);

        return emergency;
    }

    @Transactional
    public boolean sendNotificationTo(String receiverId, Emergency emergency){
        FCMNotificationDTO message = makeMessage(receiverId, emergency);
        return fcmService.SendNotificationByToken(message) != null;
    }

    private FCMNotificationDTO makeMessage(String receiverId, Emergency emergency) {
        return FCMNotificationDTO.builder()
                .title(emergency.getTitle())
                .body(emergency.getContent())
                .receiverId(receiverId)
                .build();
    }

//    public boolean sendNotificationTo(String receiverId, EmergencyRequestDTO dto) {
//        String message = makeMessage(receiverId, dto);
//        RestTemplate restTemplate = new RestTemplate();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        try {
//            headers.set("Authorization", "Bearer " + getAccessToken());
//        } catch (IOException e) {
//            return false;
//        }
//
//        HttpEntity entity = new HttpEntity<>(message, headers);
//        String API_URL = "https://fcm.googleapis.com/v1/projects/safeguard-2f704/messages:send";
//
//        ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
//
//        if(response.getStatusCode() != HttpStatus.OK){
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
//        }
//
//        saveEmergency(message, receiverId, dto);
//        return true;
//    }
//
//    private String getAccessToken() throws IOException {
//        String firebaseConfigPath = "firebase/safeguard-2f704-firebase-adminsdk-pmiwx-0bced8bb31.json";
////        String firebaseConfigPath = "auth_fcm.json";
//
//        GoogleCredentials googleCredentials = GoogleCredentials
//                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
//                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
//
//        googleCredentials.refreshIfExpired();
//        return googleCredentials.getAccessToken().getTokenValue();
//    }
//
//    private String makeMessage(String receiverId, EmergencyRequestDTO dto) {
//        // 1. receiverId 이용해서 받을 member의 토큰 값 가져오기
//        Member foundMember = memberRepository.findById(receiverId).orElseThrow(NoSuchElementException::new);
//        String token = foundMember.getFcmToken();
//
//        // 2. dto의 childName을 이용해서 보내는 child의 정보를 가져오기
//        Child foundChild = childRepository.findBychildName(dto.getChildName());
//        if (foundChild == null) {
//            throw new NoSuchElementException();
//        }
//
//        String body = "아이 이름 : " + foundChild.getChildName();
//
//        ObjectMapper om = new ObjectMapper();
//        FcmMessageDTO fcmMessageDto = FcmMessageDTO.builder()
//                .message(FcmMessageDTO.Message.builder()
//                        .token(token)
//                        .notification(FcmMessageDTO.Notification.builder()
//                                .title(dto.getTitle())
//                                .body(body)
//                                .build()
//                        ).build()).validateOnly(false).build();
//
//        try {
//            return om.writeValueAsString(fcmMessageDto);
//        } catch (JsonProcessingException e) {
//            return null;
//        }
//    }

    public List<Emergency> getSentEmergency(String memberId) {
        Optional<Member> foundMember = memberRepository.findById(memberId);
        if (foundMember.isEmpty()){
            return null;
        }

        List<Emergency> foundEmergency = emergencyRepository.findAllBySenderId(foundMember.get());
        if (foundEmergency.isEmpty()){
            return null;
        }

        return foundEmergency;
    }

    public List<Emergency> getReceivedEmergency(String memberId) {
        List<Emergency> result = new ArrayList<>();

        List<EmergencyReceiver> foundEmergencyList = emergencyReceiverRepository.findAllByReceiverId(memberId);
        if (foundEmergencyList.isEmpty()){
            return null;
        }

        for (EmergencyReceiver received : foundEmergencyList) {
            result.add(received.getEmergency());
        }

        return result;
    }

    public boolean writeEmergency(CommentRequestDTO commentRequestDTO) {
        Optional<Member> foundMember = memberRepository.findById(commentRequestDTO.getCommentatorId());
        Optional<Emergency> foundEmergency = emergencyRepository.findById(Long.valueOf(commentRequestDTO.getEmergencyId()));
        if(foundMember.isEmpty() || foundEmergency.isEmpty()){
            return false;
        }

        Comment comment = Comment.builder()
                .commentator(foundMember.get())
                .emergency(foundEmergency.get())
                .comment(commentRequestDTO.getCommentContent())
                .build();

        commentRepository.save(comment);

        return true;
    }


    public Emergency getEmergencyDetail(String emergencyId) {
        Optional<Emergency> foundEmergency = emergencyRepository.findById(Long.valueOf(emergencyId));
        return foundEmergency.orElse(null);
    }
}
