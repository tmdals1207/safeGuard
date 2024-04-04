package com.capstone.safeGuard.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank
    @Email
    private String loginEmail;
    @NotBlank
    private String password;
    @NotBlank
    private String loginType;   // 회원의 타입 - Member, Child
}
