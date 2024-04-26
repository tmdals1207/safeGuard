package com.capstone.safeGuard.dto.request;

import lombok.Getter;

@Getter
public class VerificationEmailRequestDTO {
    private String memberId;
    private boolean isMember; //member or child
}
