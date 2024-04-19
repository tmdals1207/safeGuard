package com.capstone.safeGuard.dto.reponse;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

public class LoginResponseToken {
    private String Authorization;

    @Builder
    public LoginResponseToken(String authorization) {
        Authorization = authorization;
    }
}
