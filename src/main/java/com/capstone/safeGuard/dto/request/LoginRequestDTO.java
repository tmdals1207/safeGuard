package com.capstone.safeGuard.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank
    private String editTextID;
    @NotBlank
    private String editTextPW;
    @NotBlank
    private String loginType;   // 회원의 타입 - Member, Child
}
