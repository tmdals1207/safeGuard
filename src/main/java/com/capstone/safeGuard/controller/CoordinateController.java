package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.dto.reponse.ReadAreaResponseDTO;
import com.capstone.safeGuard.dto.request.coordinate.AddAreaDTO;
import com.capstone.safeGuard.dto.request.coordinate.DeleteAreaDTO;
import com.capstone.safeGuard.service.CoordinateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CoordinateController {
    private final CoordinateService coordinateService;

    @PostMapping("/add-safe")
    public ResponseEntity<Map<String, String>> addLivingArea(@RequestBody AddAreaDTO dto){
        HashMap<String, String> result = new HashMap<>();

        if(! coordinateService.addLivingArea(dto)){
            return addErrorStatus(result);
        }

        return addOkStatus(result);
    }


    @PostMapping("/add-dangerous")
    public ResponseEntity<Map<String, String>> addForbiddenArea(@RequestBody AddAreaDTO dto){
        HashMap<String, String> result = new HashMap<>();

        if(! coordinateService.addForbiddenArea(dto)){
            return addErrorStatus(result);
        }

        return addOkStatus(result);
    }

    @PostMapping("/delete-area")
    public ResponseEntity<Map<String, String>> deleteArea(@RequestBody DeleteAreaDTO dto){
        HashMap<String, String> result = new HashMap<>();

        if(! coordinateService.deleteArea(dto)){
            return addErrorStatus(result);
        }

        return addOkStatus(result);
    }

    // TODO 위험, 안전 구역 표시
    //  map<areaId, responseDTO> 만들어서 넘김
    @PostMapping("read-areas")
    public ResponseEntity<Map<String, ReadAreaResponseDTO>> readAreas(@RequestBody String memberId){
        HashMap<String, ReadAreaResponseDTO> result = new HashMap<>();

        // 1. memberID에 저장되어 있는 coordinate 불러오기
        // 2. responseDTO로 변경
        // 3. json 형식으로 포매팅

        return ResponseEntity.ok().build();
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
