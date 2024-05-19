package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.*;
import com.capstone.safeGuard.dto.request.emergency.FcmMessageDTO;
import com.capstone.safeGuard.repository.ConfirmRepository;
import com.capstone.safeGuard.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmService {
    private final ConfirmRepository confirmRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Confirm saveConfirm(Child child, Helping helping, String confirmType) {
        Confirm confirm = new Confirm();
        if( confirmType.equals("ARRIVED") ){
            confirm.setConfirmType(ConfirmType.ARRIVED);
        } else if( confirmType.equals("DEPART") ){
            confirm.setConfirmType(ConfirmType.DEPART);
        } else {
            confirm.setConfirmType(ConfirmType.UNCONFIRMED);
        }

        confirm.setChild(child);
        confirm.setTitle(confirmType);
        confirm.setContent("아이 이름 : " + child.getChildName());
        confirm.setCreatedAt(LocalDateTime.now());
        confirm.setHelping_id(helping);
        confirmRepository.save(confirm);

        return confirm;
    }

    public boolean sendNotificationTo(String receiverId, Confirm confirm){
        // TODO fcm 테스트

        String message = makeMessage(receiverId, confirm);
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

        if(response.getStatusCode() != HttpStatus.OK){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return true;
    }

    private String makeMessage(String receiverId, Confirm confirm) {
        // 1. receiverId 이용해서 받을 member의 토큰 값 가져오기
        Member foundMember = memberRepository.findById(receiverId).orElseThrow(NoSuchElementException::new);
        String token = foundMember.getFcmToken();

        ObjectMapper om = new ObjectMapper();
        FcmMessageDTO fcmMessageDto = FcmMessageDTO.builder()
                .message(FcmMessageDTO.Message.builder()
                        .token(token)
                        .notification(FcmMessageDTO.Notification.builder()
                                .title(confirm.getTitle())
                                .body(confirm.getContent())
                                .build()
                        ).build()).validateOnly(false).build();
        try {
            return om.writeValueAsString(fcmMessageDto);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/safeguard-2f704-firebase-adminsdk-pmiwx-0bced8bb31.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

}
