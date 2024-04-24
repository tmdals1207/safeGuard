package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.dto.request.emergency.EmergencyRequestDTO;
import com.capstone.safeGuard.repository.ChildRepository;
import com.capstone.safeGuard.repository.EmergencyRepository;
import com.capstone.safeGuard.repository.MemberRepository;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EmergencyService {
    private final EmergencyRepository emergencyRepository;
    private final MemberRepository memberRepository;
    private final ChildRepository childRepository;

    public ArrayList<String> compareCoordinate(EmergencyRequestDTO emergencyRequestDto, Map<String, int[]> memberIdCoordinateHashMap, int distance) {
        ArrayList<String> neighborMemberList = new ArrayList<>();

        for (Map.Entry<String, int[]> entry : memberIdCoordinateHashMap.entrySet()) {
            if (isNeighbor(emergencyRequestDto.getLatitude(), emergencyRequestDto.getLongitude(), entry.getValue(), distance)) {
                neighborMemberList.add(entry.getKey());
            }
        }

        return neighborMemberList;
    }

    private boolean isNeighbor(double latitude, double longitude, int[] memberCoordinate, int length) {
        double x_coordinate = latitude - memberCoordinate[0];
        double y_coordinate = longitude - memberCoordinate[1];

        // 좌표 -> km
        double distance = convertCoordinateToKm(x_coordinate, y_coordinate);

        return distance <= (length);
    }

    private double convertCoordinateToKm(double x_coordinate, double y_coordinate) {
        // 위도 35~38도(한국) 기준으로
        // 대략 위도는 1도당 111km, 경도는 1도당 89km
        double x_km = 111 * x_coordinate;
        double y_km = 89 * y_coordinate;

        return Math.sqrt( (x_km * x_km) + (y_km * y_km) );
    }

    public void saveEmergency(EmergencyRequestDTO emergencyRequestDto) {
        // Emergency table에 저장
        Member member = memberRepository.findById(emergencyRequestDto.getMemberId()).orElseThrow(NoSuchElementException::new);
        // TODO childRepo에서 findByChildName을 optional 객체로 받는것에 대해서 상의
        Child child = childRepository.findBychildName(emergencyRequestDto.getChildName());
        emergencyRepository.save(emergencyRequestDto.dtoToDomain(member, child));
    }

    public int[] requestLocation(Member member) {
        // TODO member들의 위치를 요청 -> 응답으로 받은 위치를 int[]{x, y} 저장


        return null;
    }

    public boolean sendNotificationTo(String memberId, EmergencyRequestDTO dto) {
        // TODO member에게 알림 보내기 테스트
        String message = makMessage(memberId, dto);
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            headers.set("Authorization", "Bearer " + getAccessToken());
        } catch (IOException e) {
            return false;
        }

        HttpEntity entity = new HttpEntity<>(message, headers);

        String API_URL = "<https://fcm.googleapis.com/v1/projects/safeguard-2f704/messages:send>";
        ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

        return response.getStatusCode() == HttpStatus.OK;
    }

    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/safeguard-2f704-firebase-adminsdk-pmiwx-0bced8bb31.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("<https://www.googleapis.com/auth/cloud-platform>"));

        googleCredentials.refreshIfExpired();
        return  googleCredentials.getAccessToken().getTokenValue();
    }

    private String makMessage(String memberId, EmergencyRequestDTO dto) {

        return null;
    }


}
