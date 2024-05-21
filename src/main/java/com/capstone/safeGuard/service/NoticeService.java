package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Notice;
import com.capstone.safeGuard.domain.NoticeLevel;
import com.capstone.safeGuard.dto.request.notification.FCMNotificationDTO;
import com.capstone.safeGuard.repository.ChildRepository;
import com.capstone.safeGuard.repository.MemberRepository;
import com.capstone.safeGuard.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;
    private final ChildRepository childRepository;
    private final FCMService fcmService;

    @Transactional
    public Notice createNotice(String receiverId, String childName, NoticeLevel noticeLevel) {
        Notice notice = new Notice();
        notice.setTitle(noticeLevel.name());
        notice.setContent("아이 이름 : " + childName);
        notice.setReceiverId(receiverId);

        if(! memberRepository.existsByMemberId(receiverId)) {
            log.info("createNotice memberId not exist!!");
            return null;
        }
        notice.setNoticeLevel(noticeLevel);

        Child child = childRepository.findByChildName(childName);

        if (child == null) {
            log.info("createNotice childName not exist!!");
            return null;
        }
        notice.setChild(child);
        notice.setCreatedAt(LocalDateTime.now());

        noticeRepository.save(notice);

        return notice;
    }

    public boolean sendNotificationTo(Notice notice){
        FCMNotificationDTO message = makeMessage(notice);
        return fcmService.SendNotificationByToken(message) != null;
    }

    private FCMNotificationDTO makeMessage(Notice notice) {
        return FCMNotificationDTO.builder()
                .title(notice.getTitle())
                .body(notice.getContent())
                .receiverId(notice.getReceiverId())
                .build();
    }

// public boolean sendNotificationTo(Notice notice){
//        String receiverId = notice.getReceiverId();
//        String message = makeMessage(receiverId, notice);
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
//        return true;
//    }

//private String makeMessage(String receiverId, Notice notice) {
//        // 1. receiverId 이용해서 받을 member의 토큰 값 가져오기
//        Member foundMember = memberRepository.findById(receiverId).orElseThrow(NoSuchElementException::new);
//        String token = foundMember.getFcmToken();
//
//        ObjectMapper om = new ObjectMapper();
//        FcmMessageDTO fcmMessageDto = FcmMessageDTO.builder()
//                .message(FcmMessageDTO.Message.builder()
//                        .token(token)
//                        .notification(FcmMessageDTO.Notification.builder()
//                                .title(notice.getTitle())
//                                .body(notice.getContent())
//                                .build()
//                        ).build()).validateOnly(false).build();
//        try {
//            return om.writeValueAsString(fcmMessageDto);
//        } catch (JsonProcessingException e) {
//            return null;
//        }
//    }

//    private String getAccessToken() throws IOException {
//        String firebaseConfigPath = "firebase/safeguard-2f704-firebase-adminsdk-pmiwx-0bced8bb31.json";
//
//        GoogleCredentials googleCredentials = GoogleCredentials
//                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
//                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
//
//        googleCredentials.refreshIfExpired();
//        return googleCredentials.getAccessToken().getTokenValue();
//    }
}
