package com.capstone.safeGuard.dto.request;

import lombok.Data;

@Data
public class VerificationEmailDTO {
    private String inputId;
    private String inputCode;
}
