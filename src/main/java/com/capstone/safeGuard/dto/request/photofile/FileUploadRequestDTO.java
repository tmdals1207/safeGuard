package com.capstone.safeGuard.dto.request.photofile;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class FileUploadRequestDTO {
    private String uploaderType;
    private String uploaderId;
}
