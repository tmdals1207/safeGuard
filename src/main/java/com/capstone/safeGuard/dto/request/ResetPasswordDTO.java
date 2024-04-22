package com.capstone.safeGuard.dto.request;

import lombok.Getter;

@Getter
public class ResetPasswordDTO {
    private String memberId;
    private String newPassword;
}
