package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Coordinate;
import com.capstone.safeGuard.domain.Notice;
import com.capstone.safeGuard.domain.NoticeLevel;
import com.capstone.safeGuard.repository.NoticeRepository;
import com.capstone.safeGuard.service.FcmService;
import com.capstone.safeGuard.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NoticeController {
    private final MemberService memberService;
    private final FcmService fcmService;
    private final NoticeRepository noticeRepository;

    public ResponseEntity<Map<String, String>> sendNotice(String childName) {
        Map<String, String> result = new HashMap<>();
        Child foundChild = memberService.findChildByChildName(childName);
        return ResponseEntity.ok(result);
    }

    private boolean sendNoticeToMember(String receiverId, String childName, NoticeLevel noticeLevel, String message) {

        return true;
    }

    public static boolean isPointInPolygon(double[][] polygon, double[] point) {
        int n = polygon.length;
        double x = point[0];
        double y = point[1];

        boolean inside = false;
        for (int i = 0, j = n - 1; i < n; j = i++) {
            double xi = polygon[i][0], yi = polygon[i][1];
            double xj = polygon[j][0], yj = polygon[j][1];

            boolean intersect = ((yi > y) != (yj > y)) &&
                    (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
            if (intersect) {
                inside = !inside;
            }
        }

        return inside;
    }

    private static ResponseEntity<Map<String, String>> addOkStatus(Map<String, String> result) {
        result.put("status", "200");
        return ResponseEntity.ok().body(result);
    }

    private static ResponseEntity<Map<String, String>> addErrorStatus(Map<String, String> result) {
        result.put("status", "400");
        return ResponseEntity.status(400).body(result);
    }
}
