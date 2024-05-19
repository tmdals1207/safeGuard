package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.domain.Child;
import com.capstone.safeGuard.domain.Coordinate;
import com.capstone.safeGuard.domain.NoticeLevel;
import com.capstone.safeGuard.domain.Parenting;
import com.capstone.safeGuard.repository.NoticeRepository;
import com.capstone.safeGuard.service.MemberService;
import com.capstone.safeGuard.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NoticeController {
    private final MemberService memberService;
    private final NoticeService noticeService;
    private final NoticeRepository noticeRepository;

    public String sendNotice(String childName) {
        Child foundChild = memberService.findChildByChildName(childName);
        if (foundChild == null) {
            return "에러 : NoSuchChild";
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
        if (! currentStatus.equals("위험구역")) {
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

        List<Parenting> childParentingList = foundChild.getParentingList();
        // 구역 변경 시 FCM 메시지 전송
        if (currentStatus.equals("위험구역") && !"위험구역".equals(foundChild.getLastStatus())) {
            for (Parenting parenting : childParentingList) {
                if(! sendNoticeToMember(parenting.getParent().getMemberId(), foundChild, NoticeLevel.WARN)){
                    return "에러 : 전송 실패";
                }
            }
            // 마지막 상태 갱신
            foundChild.setLastStatus(currentStatus);
            return "전송 완료";
        } else if ((currentStatus.equals("일반구역") || currentStatus.equals("안전구역")) && "위험구역".equals(foundChild.getLastStatus())) {
            for (Parenting parenting : childParentingList) {
                if(! sendNoticeToMember(parenting.getParent().getMemberId(), foundChild, NoticeLevel.INFO)){
                    return "에러 : 전송 실패";
                }
            }
            // 마지막 상태 갱신
            foundChild.setLastStatus(currentStatus);
            return "전송 완료";
        }

        return null;
    }

    public boolean sendNoticeToMember(String receiverId, Child child, NoticeLevel noticeLevel) {
        // TODO fcm을 이용한 sendNotice

        // notice 저장
        noticeService.saveNotice(child, noticeLevel);

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
}

