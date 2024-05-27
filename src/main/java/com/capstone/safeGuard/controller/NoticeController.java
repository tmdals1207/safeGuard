package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.domain.*;
import com.capstone.safeGuard.dto.request.fatal.FatalRequest;
import com.capstone.safeGuard.dto.request.signupandlogin.GetIdDTO;
import com.capstone.safeGuard.dto.response.FindNotificationResponse;
import com.capstone.safeGuard.repository.ChildRepository;
import com.capstone.safeGuard.repository.CoordinateRepository;
import com.capstone.safeGuard.service.MemberService;
import com.capstone.safeGuard.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NoticeController {
    private final MemberService memberService;
    private final NoticeService noticeService;
    private final ChildRepository childRepository;
    private final CoordinateRepository coordinateRepository;

    @PostMapping("/received-notice")
    public ResponseEntity<Map<String, FindNotificationResponse>> receivedNotice(@RequestBody GetIdDTO dto) {
        HashMap<String, FindNotificationResponse> result = new HashMap<>();

        List<Notice> noticeList = noticeService.findNoticeByMember(dto.getId());
        if (noticeList == null || noticeList.isEmpty()) {
            return ResponseEntity.status(400).body(result);
        }
        for (Notice notice : noticeList) {
            result.put(notice.getNoticeId() + "",
                    FindNotificationResponse.builder()
                            .type(notice.getNoticeLevel() + "")
                            .child(notice.getChild().getChildName())
                            .title(notice.getTitle())
                            .content(notice.getContent())
                            .date(notice.getCreatedAt().toString())
                            .build()
            );
        }

        return ResponseEntity.ok().body(result);
    }

    @Transactional
    public void sendNotice(String childName) {
        log.warn("sendNotice 호출: {}", childName);
        Child foundChild = memberService.findChildByChildName(childName);
        if (foundChild == null) {
            log.warn("에러 : foundChild is null!!");
            return;
        }

        String currentStatus = getCurrentStatus(foundChild);
        String lastStatus = foundChild.getLastStatus();
        log.warn("{}의 currentStatus : {} | lastStatus : {} ", childName, currentStatus, foundChild.getLastStatus());


        List<Parenting> childParentingList = foundChild.getParentingList();
        // 구역 변경 시 FCM 메시지 전송
        if (currentStatus.equals("위험구역") && (!lastStatus.equals("위험구역"))) {
            if (!sendNoticeToMember(childParentingList, foundChild.getChildName(), NoticeLevel.WARN)) {
                log.warn("에러 : 전송 실패");
                return;
            }
            // 마지막 상태 갱신
            foundChild.setLastStatus(currentStatus);
            log.warn("warn 전송 완료");
            return;
        } //else if ( lastStatus.equals("위험구역") && (currentStatus.equals("일반구역") || currentStatus.equals("안전구역")) ) {
        else if ( !lastStatus.equals(currentStatus) ) {
            if (!sendNoticeToMember(childParentingList, foundChild.getChildName(), NoticeLevel.INFO)) {
                log.warn("에러 : 전송 실패");
                return;
            }
            // 마지막 상태 갱신
            foundChild.setLastStatus(currentStatus);
            log.warn("info 전송 완료");
        }
    }

    @Transactional
    public String getCurrentStatus(Child foundChild) {
        log.warn("getCurrentStatus");
        double[] childPosition = {foundChild.getLatitude(), foundChild.getLongitude()};

        ArrayList<Coordinate> coordinateArrayList = coordinateRepository.findAllByChild(foundChild);
        for (Coordinate coordinate : coordinateArrayList) {
            double[][] polygon = {
                    {coordinate.getYOfNorthWest(), coordinate.getXOfNorthWest()},
                    {coordinate.getYOfNorthEast(), coordinate.getXOfNorthEast()},
                    {coordinate.getYOfSouthEast(), coordinate.getXOfSouthEast()},
                    {coordinate.getYOfSouthWest(), coordinate.getXOfSouthWest()}
            };

            if(coordinate.isLivingArea()){
                if(isPointInPolygon(polygon, childPosition)){
                    return "안전구역";
                }
            } else {
                if(isPointInPolygon(polygon, childPosition)){
                    return "위험구역";
                }
            }
        }

        return "일반구역";
/*
        // 위험 구역 점검
        for (Coordinate forbiddenArea : foundChild.getForbiddenAreas()) {
            double[][] polygon = {
                    {forbiddenArea.getYOfNorthWest(), forbiddenArea.getXOfNorthWest()},
                    {forbiddenArea.getYOfNorthEast(), forbiddenArea.getXOfNorthEast()},
                    {forbiddenArea.getYOfSouthEast(), forbiddenArea.getXOfSouthEast()},
                    {forbiddenArea.getYOfSouthWest(), forbiddenArea.getXOfSouthWest()}
            };

            if (isPointInPolygon(polygon, childPosition)) {
                return "위험구역";
            }
        }

        // 안전 구역 점검
        for (Coordinate livingArea : foundChild.getLivingAreas()) {
            double[][] polygon = {
                    {livingArea.getYOfNorthWest(), livingArea.getXOfNorthWest()},
                    {livingArea.getYOfNorthEast(), livingArea.getXOfNorthEast()},
                    {livingArea.getYOfSouthEast(), livingArea.getXOfSouthEast()},
                    {livingArea.getYOfSouthWest(), livingArea.getXOfSouthWest()}
            };

            if (isPointInPolygon(polygon, childPosition)) {
                return "안전구역";
            }
        }

        return "일반구역";
 */
    }

    @Transactional
    public boolean sendNoticeToMember(List<Parenting> parentingList, String childName, NoticeLevel noticeLevel) {
        for (Parenting parenting : parentingList) {
            Notice notice = noticeService.createNotice(parenting.getParent().getMemberId(),
                    childName,
                    noticeLevel);
            if (notice == null) {
                log.info("No such notice");
                return false;
            }
            return noticeService.sendNotificationTo(notice);
        }

        return true;
    }


    public static boolean isPointInPolygon(double[][] polygon, double[] point) {
        int n = polygon.length;
        double px = point[0], py = point[1];
        boolean inside = false;

        for (int i = 0, j = n - 1; i < n; j = i++) {
            double ix = polygon[i][0], iy = polygon[i][1];
            double jx = polygon[j][0], jy = polygon[j][1];

            if ((iy > py) != (jy > py) &&
                    (px < (jx - ix) * (py - iy) / (jy - iy) + ix)) {
                inside = !inside;
            }
        }

        return inside;
    }

    @PostMapping("/fatal")
    @Transactional
    public ResponseEntity<Map<String, String>> fatal(@RequestBody FatalRequest dto) {
        Map<String, String> result = new HashMap<>();
        Child foundChild = childRepository.findBychildName(dto.getChildName());

        List<Parenting> childParentingList = foundChild.getParentingList();
        if (!sendNoticeToMember(childParentingList, foundChild.getChildName(), NoticeLevel.FATAL)) {
            log.info("send fatal 실패");
            return addErrorStatus(result);
        }

        return addOkStatus(result);
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

