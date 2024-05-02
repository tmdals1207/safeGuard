package com.capstone.safeGuard.dto.reponse;

import lombok.Builder;

public class LoginResponseToken {
    private String Authorization;

    @Builder
    public LoginResponseToken(String authorization) {
        Authorization = authorization;
    }
}
