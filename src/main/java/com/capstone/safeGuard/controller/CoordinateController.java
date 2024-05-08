package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.dto.request.coordinate.AddAreaDTO;
import com.capstone.safeGuard.service.CoordinateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CoordinateController {
    private final CoordinateService coordinateService;

    // TODO childId 받는 부분 childName으로 변경

    @PostMapping("/{child-id}/add-safe")
    public ResponseEntity<Map<String, String>> addLivingArea(
            @PathVariable("child-id") String childId,
            AddAreaDTO dto){
        HashMap<String, String> result = new HashMap<>();

        if(dto.getChildId() != Long.parseLong(childId)){
            return addErrorStatus(result);
        }

        if(! coordinateService.addLivingArea(dto)){
            return addErrorStatus(result);
        }

        return addOkStatus(result);
    }


    @PostMapping("/{child-id}/add-dangerous")
    public ResponseEntity<Map<String, String>> addForbiddenArea(
            @PathVariable("child-id") String childId,
            AddAreaDTO dto){
        HashMap<String, String> result = new HashMap<>();

        if(dto.getChildId() != Long.parseLong(childId)){
            return addErrorStatus(result);
        }

        if(! coordinateService.addForbiddenArea(dto)){
            return addErrorStatus(result);
        }

        return addOkStatus(result);
    }

    @PostMapping("/{child-id}/delete-area/{area-id}")
    public ResponseEntity<Map<String, String>> deleteArea(
            @PathVariable("child-id") String childId,
            @PathVariable("area-id") String areaId){
        HashMap<String, String> result = new HashMap<>();

        if(! coordinateService.deleteArea(Long.parseLong(childId), Long.parseLong(areaId))){
            return addErrorStatus(result);
        }

        return addOkStatus(result);
    }

    // TODO 위험, 안전 구역 표시
    //  map<areaId, map<bool, string>> 만들어서 넘김

    private static ResponseEntity<Map<String, String>> addOkStatus(Map<String, String> result) {
        result.put("status", "200");
        return ResponseEntity.ok().body(result);
    }

    private static ResponseEntity<Map<String, String>> addErrorStatus(Map<String, String> result) {
        result.put("status", "400");
        return ResponseEntity.status(400).body(result);
    }
}
