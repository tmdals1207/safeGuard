package com.capstone.safeGuard.controller;

import com.capstone.safeGuard.dto.request.photofile.FileUploadRequestDTO;
import com.capstone.safeGuard.dto.request.photofile.GetFileRequestDTO;
import com.capstone.safeGuard.service.FileService;
import com.capstone.safeGuard.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

@Controller@RequiredArgsConstructor @Slf4j
public class FileController {
    private final MemberService memberService;
    private final FileService fileService;


    @PostMapping("/upload-file")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestBody FileUploadRequestDTO dto) {
        Map<String, String> result = new HashMap<>();
        String uploaderType = dto.getUploaderType();

        String filePath;

        if(uploaderType.equalsIgnoreCase("Member")){
            filePath = fileService.saveMemberFile(dto.getFile(), dto.getUploaderId());
            if(filePath == null || filePath.isEmpty()){
                return addErrorStatus(result);
            }
        } else if (uploaderType.equalsIgnoreCase("Child")) {
            filePath = fileService.saveChildFile(dto.getFile(), dto.getUploaderId());
            if(filePath == null || filePath.isEmpty()){
                return addErrorStatus(result);
            }
        } else {
            return addErrorStatus(result);
        }
        result.put("filePath", filePath);
        return addOkStatus(result);
    }

    @PostMapping("/get-file")
    public ResponseEntity<Map<String, String>> getFile(@RequestBody GetFileRequestDTO dto) {
        Map<String, String> result = new HashMap<>();

        String userType = dto.getUserType();
        String filePath;
        if(userType.equalsIgnoreCase("Member")){
            filePath = fileService.findMemberFile(dto.getUserId());
            if(filePath == null || filePath.isEmpty()){
                return addErrorStatus(result);
            }
        } else if (userType.equalsIgnoreCase("Child")) {
            filePath = fileService.findChildFile(dto.getUserId());
            if(filePath == null || filePath.isEmpty()){
                return addErrorStatus(result);
            }
        } else {
            return addErrorStatus(result);
        }

        result.put("filePath", filePath);
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
