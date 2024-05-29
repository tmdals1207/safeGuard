package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.service.NaverSpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/speech")
public class SpeechController {

    @Autowired
    private NaverSpeechService naverSpeechService;

    @PostMapping("/transcribe")
    public String transcribe(@RequestParam("file") MultipartFile file) {
        try {
            return naverSpeechService.transcribe(file);
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to transcribe audio.";
        }
    }
}
