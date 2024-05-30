package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.service.NaverSpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/speech")
public class SpeechController {

    @Autowired
    private NaverSpeechService naverSpeechService;

    @PostMapping("/transcribe-and-compare")
    public String transcribeAndCompare(@RequestParam("file") MultipartFile file) {
        try {
            return naverSpeechService.transcribeAndCompare(file);
        } catch (IOException e) {
            e.printStackTrace();
            return "오디오 변환 및 비교에 실패했습니다.";
        }
    }
}
