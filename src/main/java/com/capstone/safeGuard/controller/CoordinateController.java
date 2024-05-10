package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.domain.Coordinate;
import com.capstone.safeGuard.dto.reponse.ReadAreaResponseDTO;
import com.capstone.safeGuard.dto.request.coordinate.AddAreaDTO;
import com.capstone.safeGuard.dto.request.coordinate.DeleteAreaDTO;
import com.capstone.safeGuard.service.CoordinateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
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

    @PostMapping("read-areas")
    public ResponseEntity<Map<String, ReadAreaResponseDTO>> readAreas(@RequestBody String childName){
        HashMap<String, ReadAreaResponseDTO> result = new HashMap<>();

        // 1. childID에 저장되어 있는 coordinate 불러오기
        ArrayList<Coordinate> coordinates = coordinateService.readAreasByChild(childName);

        // 2. responseDTO로 변경
        for (Coordinate coordinate : coordinates) {
            result.put(coordinate.getCoordinateId()+"",
                    ReadAreaResponseDTO.builder()
                            .isLiving(coordinate.isLivingArea() + "")
                            .firstX(coordinate.getXOfNorthEast() + "")
                            .firstY(coordinate.getYOfNorthEast() + "")
                            .secondX(coordinate.getXOfNorthWest() + "")
                            .secondY(coordinate.getYOfNorthWest() + "")
                            .thirdX(coordinate.getXOfSouthWest() + "")
                            .thirdY(coordinate.getYOfSouthWest() + "")
                            .fourthX(coordinate.getXOfSouthEast() + "")
                            .fourthY(coordinate.getYOfSouthEast() + "")
                            .build()
            );
        }

        return ResponseEntity.ok().body(result);
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
