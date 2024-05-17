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
        if (foundChild == null) {
            result.put("message", "해당하는 아이가 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }

        double[] childPosition = {foundChild.getLatitude(), foundChild.getLongitude()};
        String currentStatus = "일반구역";

        // 위험 구역 점검
        for (Coordinate forbiddenArea : foundChild.getForbiddenAreas()) {
            double[][] polygon = {
                    {forbiddenArea.getXOfNorthWest(), forbiddenArea.getYOfNorthWest()},
                    {forbiddenArea.getXOfNorthEast(), forbiddenArea.getYOfNorthEast()},
                    {forbiddenArea.getXOfSouthEast(), forbiddenArea.getYOfSouthEast()},
                    {forbiddenArea.getXOfSouthWest(), forbiddenArea.getYOfSouthWest()}
            };

            if (isPointInPolygon(polygon, childPosition)) {
                currentStatus = "위험구역";
                break;
            }
        }

        // 안전 구역 점검
        if (!currentStatus.equals("위험구역")) {
            for (Coordinate livingArea : foundChild.getLivingAreas()) {
                double[][] polygon = {
                        {livingArea.getXOfNorthWest(), livingArea.getYOfNorthWest()},
                        {livingArea.getXOfNorthEast(), livingArea.getYOfNorthEast()},
                        {livingArea.getXOfSouthEast(), livingArea.getYOfSouthEast()},
                        {livingArea.getXOfSouthWest(), livingArea.getYOfSouthWest()}
                };

                if (isPointInPolygon(polygon, childPosition)) {
                    currentStatus = "안전구역";
                    break;
                }
            }
        }

        // 구역 변경 시 FCM 메시지 전송
        if (currentStatus.equals("위험구역") && !"위험구역".equals(foundChild.getLastStatus())) {
            sendNoticeToMember("parentId", childName, NoticeLevel.WARN, "아이가 위험구역에 있습니다.");
        } else if ((currentStatus.equals("일반구역") || currentStatus.equals("안전구역")) && "위험구역".equals(foundChild.getLastStatus())) {
            sendNoticeToMember("parentId", childName, NoticeLevel.INFO, "아이가 안전구역 또는 일반구역으로 이동했습니다.");
        }

        // 마지막 상태 갱신
        foundChild.setLastStatus(currentStatus);

        result.put("status", currentStatus);
        result.put("message", currentStatus.equals("위험구역") ? "아이가 위험구역에 있습니다." : currentStatus.equals("안전구역") ? "아이가 안전 구역에 있습니다." : "아이가 일반 구역에 있습니다.");
        return ResponseEntity.ok(result);
    }

    private boolean sendNoticeToMember(String receiverId, String childName, NoticeLevel noticeLevel, String message) {
        //TODO fcmsercive와 연동 필요
        fcmService.sendFcm(receiverId, noticeLevel.name(), message);

        // notice 저장
        //TODO notice 수정 필요
        Notice notice = new Notice(receiverId, childName, noticeLevel, message);
        noticeRepository.save(notice);

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
