package com.capstone.safeGuard.dto.request.confirm;

import lombok.Getter;

@Getter
public class SendConfirmRequest{
    private String senderId;
    private String childName;
    private String confirmType;
}
