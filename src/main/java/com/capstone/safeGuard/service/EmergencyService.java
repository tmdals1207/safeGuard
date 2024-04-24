package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Member;
import com.capstone.safeGuard.dto.request.emergency.EmergencyDTO;
import com.capstone.safeGuard.repository.ChildRepository;
import com.capstone.safeGuard.repository.EmergencyRepository;
import com.capstone.safeGuard.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class EmergencyService {
    private final EmergencyRepository emergencyRepository;
    private final MemberRepository memberRepository;
    private final ChildRepository childRepository;

    public ArrayList<String> compareCoordinate(EmergencyDTO emergencyDto, Map<String, int[]> memberIdCoordinateHashMap, int distance) {
        ArrayList<String> neighborMemberList = new ArrayList<>();

        for (Map.Entry<String, int[]> entry : memberIdCoordinateHashMap.entrySet()) {
            if (isNeighbor(emergencyDto.getLatitude(), emergencyDto.getLongitude(), entry.getValue(), distance)) {
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

    public void saveEmergency(EmergencyDTO emergencyDto) {
        // Emergency table에 저장
        Member member = memberRepository.findById(emergencyDto.getMemberId()).orElseThrow(NoSuchElementException::new);
        // TODO childRepo에서 findByChildName을 optional 객체로 받는것에 대해서 상의
        Child child = childRepository.findBychildName(emergencyDto.getChildName());
        emergencyRepository.save(emergencyDto.dtoToDomain(member, child));
    }

}
