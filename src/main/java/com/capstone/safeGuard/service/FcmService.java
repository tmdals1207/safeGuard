package com.capstone.safeGuard.service;

import com.capstone.safeGuard.domain.Member;
import org.springframework.stereotype.Service;

@Service
public class FcmService {
    public int[] requestLocation(Member member) {
        // TODO member들의 위치를 요청 -> 응답으로 받은 위치를 int[]{x, y} 저장
        return null;
    }

    public void sendNotification(String memberId) {
        // TODO member에게 알림을 보냄

    }
}
